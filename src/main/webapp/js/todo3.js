var todo = {
    init: function () {
        var token = $.cookie("session");
        if (token) {
            $('#user').text($.cookie("user"));

            $('#search').typeahead({
                name: 'titles',
                remote: 'rest/item/suggest/?text=%QUERY&token=' + token
            });
        }

        Hint.init({
            "selector": ".bb-alert"
        });

        $("#logout").on("click", function () {
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
                    contentType: "application/json; charset=utf-8",
                    dataType: "text",
                    success: function (data) {
                        $('#user').text(nickname);
                        token = data;
                        $.cookie("session", data);
                        $.cookie("user", nickname);

                        $('#search').typeahead({
                            name: 'titles',
                            remote: 'rest/item/suggest/?text=%QUERY&token=' + token
                        });

                        $('#MyGrid').datagrid('reload');
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
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

        // INITIALIZING THE DATAGRID
        var dataSource = {
            columns: function () {
                return [
                    {
                        property: 'priority',
                        label: 'Priority',
                        sortable: true
                    },
                    {
                        property: 'title',
                        label: 'Title',
                        sortable: false
                    },
                    {
                        property: 'date',
                        label: 'Due Date',
                        sortable: true
                    },
                    {
                        property: 'description',
                        label: 'Description',
                        sortable: false
                    },
                    {
                        property: 'completed',
                        label: 'Completed',
                        sortable: false
                    }
                ];
            },
            data: function (options, callback) {
                var self = this;

                setTimeout(function () {
                    var data = $.extend(true, [], self._data);

                    // PAGING
                    var startIndex = options.pageIndex * options.pageSize;
                    var endIndex = startIndex + options.pageSize;

                    token = $.cookie("session");

                    var params = {
                        'text': ((options.search) ? (options.search) : '') + '*',
                        'start': startIndex,
                        'size': options.pageSize,
                        'sort': (options.sortDirection) ? options.sortDirection : 'asc',
                        'sortBy': (options.sortProperty) ? options.sortProperty : 'date',
                        'token': token
                    };

                    if (options.filter) {
                        if (options.filter.value == 'completed')
                            params.completed = true;
                        if (options.filter.value == 'notCompleted')
                            params.completed = false;
                    }

                    $.ajax({
                        url: "rest/item/filter",
                        async: false,
                        type: "GET",
                        data: params,
                        accepts: {
                            text: "application/json"
                        },
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (items) {
                            $.each(items, function (key, value) {
                                items[key].completed = (value.completed) ? 'Yes' : 'No';
                                var date = new Date(value.date);
                                items[key].date = date.getFullYear() + " / " + date.getMonth() + " / " + date.getDate();
                            });
                            data = items;
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Hint.show(errorThrown);
                        }
                    });

                    var count = data.length;

                    // PAGING
                    var end = (endIndex > count) ? count : endIndex;
                    var pages = Math.ceil(count / options.pageSize);
                    var page = options.pageIndex + 1;
                    var start = startIndex + 1;

                    callback({ data: data, start: start, end: end, count: count, pages: pages, page: page });

                }, this._delay)
            }
        }
   }
}