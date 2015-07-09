onload = function()
{
    $("#error").hide();
    $("#submit").bind("click", register);
};

var register = function(event) {
    event.preventDefault();
    
    var user = {};
    user.username = $("#username").val();
    user.password = $("#password").val();
   
   var request = new XMLHttpRequest();
    request.open("POST", "http://" + HOST +":8080/animeList/api/users");
    request.onload = function() {
        if (request.status === 201) {
            window.location.replace("http://" + HOST +":8080/animeList/index.html");
        } else {
            setErrorMessage("Unable to add user!");
        }
    };
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(user));
   
}


