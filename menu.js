$(document).ready(function(){
    $('li a').click(function () {
        $('li').removeClass('active');
        $('li a').removeClass('active');
        $(this).parents('li').addClass('active');
        $(this).addClass('active');
    });
    $('[data-toggle="offcanvas"]').click(function () {

        if ($('.sidebar-offcanvas').hasClass('active')) {
            $('.bs-docs-sidebar').animate({ right: '-210px' });
            setTimeout(function () {
                $('.sidebar-offcanvas').removeClass('active');
            }, 300);          
        }
        else {
            $('.bs-docs-sidebar').animate({ right: '0' });
            $('.sidebar-offcanvas').addClass('active');         
        }
    });
    $(window).resize(function () {
        if($(window).width()>751){
            $('.bs-docs-sidebar').css('right', '0');
            $('.sidebar-offcanvas').addClass('active');
        }
        else{
            $('.sidebar-offcanvas').removeClass('active');
            $('.bs-docs-sidebar').css( 'right', '-210px' );
        }
    });
});
