package com.emploi.temps.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.emploi.temps.service.ResultaFetService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.emploi.db.ResultatFet;
import com.hakibati.emploi.db.repo.ResultatFetRepository;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Singleton
@Transactional
public class ResultatFetController {
  @Inject ResultatFetRepository resultatFetRepository;
  //  private Logger log = LoggerFactory.getLogger(ProfController.class);

  String USER_ADMIN_GROUP = "admins";

  public void OpenImportResultaFet(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (resultatFetRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      ResultatFet resultatFet = new ResultatFet();
      resultatFet.setName(user.getEtablissementSelectionnee().getName());
      Beans.get(ResultatFetRepository.class).save(resultatFet);
    }
    response.setView(
        ActionView.define(("import FET Resultat"))
            .model("com.hakibati.emploi.db.ResultatFet")
            .add("form", "ResultatFet-form")
            .domain(
                "self.id = "
                    + resultatFetRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                resultatFetRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  @Singleton
  @Transactional
  public void ImportResulteFet(ActionRequest request, ActionResponse response)
      throws ParserConfigurationException, SAXException, IOException {
    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importFileFet")).get("id").toString()));

    Long resultaId = (Long) request.getContext().get("id");
    ResultatFet resultatFet = Beans.get(ResultatFetRepository.class).find(resultaId);
    ResultaFetService resultaFetService = new ResultaFetService();
    String res = resultaFetService.importResultaFet(metaFile, resultatFet);
    MetaFiles.getPath(metaFile).toFile().delete();
    if (res == null) response.setReload(true);
    MetaFiles.getPath(metaFile).toFile().delete();

    response.setAlert(res);
    //    response.setSignal("refresh-app", true);
  }

  public void ImportResulteFetTEST(ActionRequest request, ActionResponse response)
      throws ParserConfigurationException, SAXException, IOException {
    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importFileFet")).get("id").toString()));

    File xmlFile = MetaFiles.getPath(metaFile).toFile();
    //    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    //    DocumentBuilder dBuilder;
    //    dBuilder = dbFactory.newDocumentBuilder();
    //     Document doc;
    //     NodeList nodeList;
    //    String nodName="Day";
    //    doc = dBuilder.parse(xmlFile);
    //    doc.getDocumentElement().normalize();
    //    nodeList = doc.getElementsByTagName(nodName);
    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document document = documentBuilder.parse(xmlFile);
    String str = "";
    //        str += "\nRoot element: " + document.getDocumentElement().getNodeName();
    //        Node elemNode = document.getChildNodes().item(0);
    //        NodeList nodeList;
    //        nodeList = document.getElementsByTagName("Year");
    //        Node elemNode = document.getChildNodes().item(0);
    //        Node elemNode= nodeList.item(0);
    //        str += "\n Node Name =" + elemNode.getNodeName() ;
    //        Node nodeClasse= elemNode.getChildNodes().item(0);
    //         str += "\n Node Name =" + nodeClasse.getNodeName() ;
    //         for( int n=0 ; n< elemNode.getChildNodes().getLength() ; n++){
    //             str += "\n Node Name =" + elemNode.getChildNodes().item(n).getNodeName() ;
    //         }

    NodeList nodeListNiveau = document.getElementsByTagName("Year");
    for (int y = 0; y < nodeListNiveau.getLength(); y++) {

      Element elementYear = (Element) nodeListNiveau.item(y);
      NodeList nodeListYearName = elementYear.getElementsByTagName("Name").item(0).getChildNodes();
      Node nodeYearName = (Node) nodeListYearName.item(0);
      str += nodeYearName.getNodeValue();

      NodeList nodeListClass = nodeListNiveau.item(y).getChildNodes();
      for (int g = 0; g < nodeListClass.getLength(); g++)
        if (nodeListClass.item(g).getNodeName().equals("Group")) {
          Element elementClass = (Element) nodeListClass.item(g);
          NodeList nodeListClassName =
              elementClass.getElementsByTagName("Name").item(0).getChildNodes();
          Node nodeclassName = (Node) nodeListClassName.item(0);
          //                 str+="\n"+ nodeclassName.getNodeValue();
          NodeList nodeListSubgroup = nodeListClass.item(g).getChildNodes();
          for (int s = 0; s < nodeListSubgroup.getLength(); s++)
            if (nodeListSubgroup.item(s).getNodeName().equals("Subgroup")) {
              Element elementSubgroup = (Element) nodeListSubgroup.item(s);
              NodeList nodeListSubgroupName =
                  elementSubgroup.getElementsByTagName("Name").item(0).getChildNodes();
              Node nodeSubgroupName = (Node) nodeListSubgroupName.item(0);
              str += "\n" + nodeSubgroupName.getNodeValue();
            }
        }

      //             NodeList nodeListClass2 =
      // element.getElementsByTagName("Name").item(0).getChildNodes();
      //             for(int g=0 ; g < nodeListClass.getLength(); g++)
      //             str += nodeListClass.item(g).getNodeName();
    }

    //        NodeList nodeList2= elemNode.getChildNodes();
    //        NodeList nodeList3= nodeList.item(0

    //              str += "\nNode Content =" + elemNode.getTextContent();
    //        NamedNodeMap nodeMap = elemNode.getAttributes();
    //
    //        Node node = nodeMap.item(0);
    //        str += "attr name : " + node.getNodeName();
    //        str += "attr value : " + node.getNodeValue();

    MetaFiles.getPath(metaFile).toFile().delete();
    response.setAlert(str);
  }
}
