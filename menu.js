$(document).ready(function(){
    //$('nav ul li a').click(function () {
    //    $(this).siblings('ul').toggleClass('active');
    //});
    //$('.submenu li').click(function () {
    //    $('li').removeClass('active');
    //    $(this).parents('li').addClass('active');
    //    $(this).addClass('active');

    //});
    //$("li:not(.submenu li)").click(function () {
    //    $('li').removeClass('active');
    //    //$(this).parent('li').addClass('active');
    //    $(this).addClass('active');
    //    $(this).next("li:has(.submenu)").addClass('active');
    //   // e.preventDefault();
    //    //$(this).siblings('li').removeClass('active');
    //    //$(this).siblings('li').children('li').removeClass('active');
    //});
    $('li a').click(function () {
        $('li').removeClass('active');
        $(this).parents('li').addClass('active');
        $(this).addClass('active');

    });
    //$('li nav li').click(function () {
    //    $(this).siblings('li').removeClass('active');
    //    //$(this).parent('li').addClass('active');
    //    $(this).toggle();

        //$(this).siblings('li').removeClass('active');
        //$(this).siblings('li').children('li').removeClass('active');
    //});
    //$('nav ul li a').click(function () {
    //    $(this).children('ul').toggle();
    //});

});