var token = null;

$(function() {
    Hint.init({
        "selector": ".bb-alert"
    });

    $("#logout").on("click", function() {
        token = null;
        $('#user').text("");
        $('#myModal').modal('show');
    });

    $('#myModal').on('hidden.bs.modal', function () {
        var nickname = $('#nickname').val();
        var password = $('#password').val();

        if (nickname && password) {
            $.ajax({
                url: "rest/user/login",
                async: false,
                type: "GET",
                data: {
                    'nickname': nickname,
                    'password': password
                },
                accepts: {
                    text: "application/json"
                },
                contentType:"application/json; charset=utf-8",
                dataType:"text",
                success: function( data ) {
                    $('#user').text(nickname);
                    token=data;

                    $('#search').typeahead({
                        name: 'titles',
                        remote: 'rest/item/suggest/?text=%QUERY&token='+token
                    });
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    Hint.show(errorThrown);
                }
            });
        }
        if (!token) {
            Hint.show("Need to Sign In first");
            $('#myModal').modal('show');
        }
    })

    $('#myModal').modal('show');
});
