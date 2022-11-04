package com.setting.service.report;

public interface IExceptionMessage {

  /** Period service */
  static final String PERIOD_1 = /*$$(*/ "Years in 360 days" /*)*/;

  /** URL service */
  static final String URL_SERVICE_1 = /*$$(*/
      "Can not opening the connection to a empty URL." /*)*/;

  static final String URL_SERVICE_2 = /*$$(*/ "Url %s is malformed." /*)*/;
  static final String URL_SERVICE_3 = /*$$(*/
      "An error occurs while opening the connection. Please verify the following URL : %s." /*)*/;

  /** Template maker */
  static final String TEMPLATE_MAKER_1 = /*$$(*/ "No such template" /*)*/;

  static final String TEMPLATE_MAKER_2 = /*$$(*/ "Templating can not be empty" /*)*/;

  static final String RECORD_UNIQUE_FIELD = /*$$(*/ "This field needs to be unique." /*)*/;

  /** Pdf Tool */
  static final String BAD_COPY_NUMBER_ARGUMENT = /*$$(*/
      "The parameter copyNumber should be superior to 0." /*)*/;
}
