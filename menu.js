$(document).ready(function(){
    $('li a').click(function () {
        $('li').removeClass('active');
        $('li a').removeClass('active');
        $(this).parents('li').addClass('active');
        $(this).addClass('active');
    });
    $('[data-toggle="offcanvas"]').click(function () {
        $('.row-offcanvas').toggleClass('active')
    });
});
