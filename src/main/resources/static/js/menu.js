$(document).ready(function () {
    loadPage('ftpconfig');
    $('#menuList> a').click(function (e) {
        $('#menuList> a').removeClass('active');
        $(this).addClass('active');
    });
})

function loadPage(url) {
    $.get(url,function (data) {
        $("#right").html(data);
    })
}
