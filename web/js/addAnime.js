/**
 * GLOBALS
 */

var animes;
var titleLength = 20;

onload = function()
{
    //updateList();
    $("#added").hide();
    $("#search").focus();
    $("#search").val("naruto");
    searchAnime();
};

$("#search").keypress(function(event) {
        if (event.which == 13) {
            event.preventDefault();
            searchAnime();
        }
});

$("#btnSearch").on("click", function () {
    searchAnime(); 
});



function searchAnime() {
    var search = $("#search").val();
    var request = new XMLHttpRequest();
    search = encodeURIComponent(search);
    
    request.open("GET", "http://localhost:8080/animeList/api/animes/add/" + search);
    request.onload = function() {
        if (request.status === 200) {
             $("#following").empty();
             $('#error').hide();
            animes = JSON.parse(request.responseText);
            if (animes.length == 0) {
                setErrorMessage("No animes where found!");
            } else {
                buildList();
            }
        } else {
            $("#following").empty();
            setErrorMessage("No animes where found!");
        }
    };
    request.send(null);
}

function addAnime() {
    var search = $("#search").val();
    
    var request = new XMLHttpRequest();
    request.open("POST", "http://localhost:8080/animeList/api/animes/add/"+ search + "/" + anime.id);
    anime.id = null;
    request.onload = function() {
        if (request.status === 201) {
            $("#error").empty();
            //updateList();
        } else {
            $("#error").text("Unable to add user");
        }
    };
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(anime));
}

function buildList() {
    for (var i = 0; i < animes.length; i++) {
                    var sTitle = animes[i].title;
                    if (sTitle.length > titleLength) {
                        sTitle = sTitle.substring(0, titleLength) + "...";
                    }
                    var anime = $('<article>', {
                        'class':'serie'
                    });
                    
                    var image = $('<div>', {
                        'class':'image'
                    }).append($('<img>', {
                        'src': animes[i].img,
                        'alt': animes[i].title
                    }));
                    
                    var type = $('<span>', {
                        'class':'type'
                    }).text(animes[i].type);
                    var title = $('<h3>').text(sTitle).append(type).append($('<hr/>'));
                    var description = $('<div>').append("<p>").text(animes[i].description);

                    var detailIcon = $('<a>', {
                        'href':"#",
                        'class':'btn fa fa-info',
                        'data-id': animes[i].id,
                        'data-name' :  animes[i].title
                    }).on("click", function(){
                        
                    }).append($('<span>', {
                        'text':'Detail'
                    }));

                    var add = $('<form>', {
                        'method': '',
                        'action': ''
                    }).append($('<a>', {
                        'href' : '#',
                        'class':'btn add fa fa-plus-square',
                        'data-id': animes[i].id,
                    }).append($('<span>', {
                        'text':'Add'
                    })).on("click", function(event){
                        event.preventDefault();
                        var id = $(this).data('id');
                        var search = $("#search").val();

                        var request = new XMLHttpRequest();
                        search = encodeURIComponent(search);
                        request.open("POST", "http://localhost:8080/animeList/api/animes/add/"+ search + "/" + id);
                        anime.id = null;
                        request.onload = function() {
                            if (request.status === 201) {
                                $("#error").empty();
                                setSuccesMessage('"' + animes[id].title + '"' + " has been added to your library!");
                                //window.location.replace("http://localhost:8080/animeList/index.html");
                            } else if (request.status == 400) {
                                setErrorMessage("Anime is already in your library!");
                            }
                        };
                    request.setRequestHeader("Content-Type", "application/json");
                    request.send(JSON.stringify(animes[id]));
                }));
                
                anime.append(image).append(title).append(description).append(add).append(detailIcon);

                $("#following").append(anime);        
                }
}