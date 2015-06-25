/**
 * GLOBALS
 */
var animes;
var titleLength = 18;

onload = function()
{
    getAllAnime();
    $("#error").hide();
};

$("#search").keypress(function(event) {
        if (event.which == 13) {
            event.preventDefault();
            searchAnime();
        }
});

/**
 * Search anime in database
 * @returns {undefined}
 */
function searchAnime() {
    var search = $("#search").val();
    var request = new XMLHttpRequest();
    request.open("GET", "http://localhost:8080/animeList/api/animes/" + search);
    request.onload = function() {
        if (request.status === 200) {
             $("#following").empty();
             $("#error").hide();
            animes = JSON.parse(request.responseText);
            
            if (animes.length == 0) {
                setErrorMessage("Anime was not found!");
            } else {
                buildList(); 
            }
        } 
    };
    request.send(null);
}

function getAllAnime()
{
    var request = new XMLHttpRequest();
    request.open("GET", "http://localhost:8080/animeList/api/animes");
    request.onload = function() {
        if (request.status === 200) {
            $("#following").empty();
            $("#error").hide();
            animes = JSON.parse(request.responseText);
            buildList();      
            $('#error').css("display","hidden");
        } else {
            setErrorMessage("Unable to load animes!");
        }
    };
    request.send(null);
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
        
        var status;
        if (animes[i].status.toLowerCase() == "currently airing") {
            status = $('<span >', {
                'class':'fa fa-circle status status-airing' 
            });
        } else {
            status = $('<span >', {
                'class':'fa fa-circle status status-finished' 
            });
        }
        
        var type = $('<span>', {
            'class':'type'
        }).text(animes[i].type);
        var title = $('<h3>').text(sTitle).prepend(status).append(type).append($('<hr/>'));
        var description = $('<div>',{
            'class':'description'
        }).text(animes[i].description);
        
        
        var nextAirDate = animes[i].nextAirDate;
        if (!nextAirDate.length == 0 && nextAirDate !== "TODAY") {
            nextAirDate = $('<div>', {
                'class':'nextAirDate'
            }).countdown(animes[i].nextAirDate, function(event) {
                $(this).text(event.strftime('%D days %H:%M:%S'));
            });
        } else if(nextAirDate === "TODAY") {
            nextAirDate = $('<div>',{
               'class':'nextAirDate',
               'text':'Today'
            });
        } else {
            nextAirDate = $('<div>', {
                'class':'nextAirDate',
                'style':'display:none;'
            });
        }
        
        var detailIcon = $('<a>', {
            'href':"#",
            'class':'btn fa fa-info',
            'data-id': animes[i].id,
            'data-name' :  animes[i].title
        }).on("click", function(){
            
        }).append($('<span>', {
            'text':'Detail'
        }));
                
        var remove = $('<form>', {
            'method': '',
            'action': ''
        }).append($('<a>', {
            'href':"#",
            'class':'btn remove fa fa-trash-o',
            'data-id': animes[i].id,
            'data-name' :  animes[i].title
        }).append($("<span>", {
            'text':'Remove'
        })).on("click", function(event){
            event.preventDefault();
            var id = $(this).data('id');
            var name = $(this).data('name');
            
            if (confirm("Do you want to delete " + name)) {
                var request = new XMLHttpRequest();
                request.open("DELETE", "http://localhost:8080/animeList/api/animes/delete/"+ id);
            
                request.onload = function() {
                    if (request.status === 204) {
                        getAllAnime();
                        setSuccesMessage('"' + name + '"' + " was succesful removed!");
                    } else {
                        setErrorMessage("Unable to delete anime!");
                    }
                };
            
                request.send();
            }
        }));


        anime.append(image.append(nextAirDate)).append(title).append(description).append(remove).append(detailIcon);

        $("#following").append(anime);        
}

}