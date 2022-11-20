setInterval(hideit,200);
function hideit() {
    let link = location.href;
    let parts = link.split('#');
    if(parts[1]== "/"){
        $('#main_img').show();
    }
    else{
        $('#main_img').hide();
    }}