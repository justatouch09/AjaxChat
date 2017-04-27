function addMessage() {
    var message = {
        author: $("#author").val(), //get val of author field//dollar sign $jquery
        text: $("#text").val()
    };
    $.post(
        "/add-message",
        JSON.stringify(message),//body of our request//make an object into a string
        function(data) {        //function to be executed when the post request is complete
            $("#text").val("");
        }
    );

    function getMessages() {
        $.get(
            "/get-messages",
            function(data) {
                $("#messages").empty(); //empty html of div messages

                var messages = JSON.parse(data);  //array of messages

                for (var i in messages) {
                    var author = messages[i].author; //for every array get author
                    var text = messages[i].text;
                    var elem = $("<div>");
                    elem.text(author + ": " + text); //set text value to author and what you say plus text
                    $("#messages").append(elem); //retrieve data at get messages
                }
            }
        );
    }

    setInterval(getMessages, 1000); //run a method on a interval, first parameter method getMessages gets them displays
                                    //amount of time between running this
}