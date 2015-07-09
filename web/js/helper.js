/**
 * GLOBALS
 */

var currentAnime;
var HOST = "95.85.41.166";

$('#close').on("click", function(){
   $('#error').slideUp("slow"); 
});

$('#error').hide();

$("#update span").hover(function(i) {
    $("#update").css("color","#40C1C8");
}, function(o) {
    $("#update").css("color","#fff");
});

$("#authenticate span").hover(function(i) {
    $("#authenticate").css("color","#40C1C8");
}, function(o) {
    $("#authenticate").css("color","#fff");
});

$("#registerl span").hover(function(i) {
    $("#registerl").css("color","#40C1C8");
}, function(o) {
    $("#registerl").css("color","#fff");
});




/**
 * Error / Succes Setters
 * @param {type} message
 * @returns {undefined}
 */
var setErrorMessage = function(message) {
    $('#message').hide();
    $('#message').removeClass("succes").addClass("error");
    $('#message p').text(message);
    $('#message p').append($('<span>', {
        'id': 'close',
        'class': 'fa fa-times-circle'
    }).on("click", function(){
        $('#message').slideUp("slow"); 
    }));
    $('#message').slideDown("slow");
}

var setSuccesMessage = function(message) {
    $('#message').hide();
    $('#message').removeClass("error").addClass("succes");
    $('#message p').text(message);
    $('#message p').append($('<span>', {
        'id': 'close',
        'class': 'fa fa-times-circle'
    }).on("click", function(){
        $('#message').slideUp("slow"); 
    }));
    $('#message').slideDown("slow");
}

var heroMessage = function(message) {
    if (message == null) {
        $("#added").fadeIn("slow").delay(300).fadeOut("slow");
    } else {
        $("#added").text(message).fadeIn("slow").delay(300).fadeOut("slow");
    }
};
