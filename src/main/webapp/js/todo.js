var token = null;

/*
function dataSource() {
    var ds = {}
    ds.columns = function() {
        return [id, name];
    }

    ds.data = function(options, callback) {

    }
    return ds;
}
*/
$(function() {
    token = $.cookie("session");
    if (token) {
        $('#user').text($.cookie("user"));

        $('#search').typeahead({
            name: 'titles',
            remote: 'rest/item/suggest/?text=%QUERY&token='+token
        });

/*
        var dataSource = {
            columns: function() {
                return [
                    {
                        property: 'toponymName',
                        label: 'Name',
                        sortable: true
                    },
                    {
                        property: 'countrycode',
                        label: 'Country',
                        sortable: true
                    },
                    {
                        property: 'population',
                        label: 'Population',
                        sortable: true
                    },
                    {
                        property: 'fcodeName',
                        label: 'Type',
                        sortable: true
                    }
                ];
            },

            data: function(options, callback) {
                return sampleData.geonames;
            }
        }

        $('#MyGrid').datagrid({ dataSource: dataSource, stretchHeight: true })
        $('#MyGrid').datagrid('reload');

        $('#datagrid-reload').on('click', function () {
            $('#MyGrid').datagrid('reload');
        });

*/
    }

    Hint.init({
        "selector": ".bb-alert"
    });

    $("#logout").on("click", function() {
        $.removeCookie("session");
        $.removeCookie("user");
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
                    $.cookie("session", data);
                    $.cookie("user", nickname);

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

    if (!token)
        $('#myModal').modal('show');
});
