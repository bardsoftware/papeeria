$(document).ready(function(){
    $('li a').click(function () {
        $('li').removeClass('active');
        $('li a').removeClass('active');
        $(this).parents('li').addClass('active');
        $(this).addClass('active');
    });
    $('[data-toggle="offcanvas"]').click(function () {
        
        $('.sidebar-offcanvas').toggleClass('active');
        if ($('.sidebar-offcanvas').hasClass('active')) {
            $('.bs-docs-sidebar').animate({ right: '0' });
        }
        else {
            $('.bs-docs-sidebar').css("right", "-210px");
        }
    });
});
