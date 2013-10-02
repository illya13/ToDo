var todo = {
    token: null,

    init: function () {
        todo.initTypeahead();
        todo.initDatagrid();
        todo.initHint();
        todo.initLogout();
        todo.initLogin();

        todo.token = $.cookie("session");
        if (todo.token)
            todo.setUser($.cookie("user"));
        else
            todo.doLogin();
    },

    doLogin: function() {
        $('#login').modal('show');
    },

    setUser: function(nickname) {
        if (nickname)
            todo.ajaxUser(nickname);
        else
            $('#user').text('');
    },

    initLogout: function() {
        $("#logout").on("click", function () {
            $.removeCookie("session");
            $.removeCookie("user");

            todo.token = null;
            todo.setUser(null);

            todo.doLogin();
        });
    },

    initLogin: function() {
        $('#login').on('hidden.bs.modal', function () {
            var nickname = $('#nickname').val();
            var password = $('#password').val();

            if (nickname && password) {
                todo.token = todo.ajaxLogin(nickname, password);
            }
            if (todo.token) {
                todo.setUser(nickname);
                todo.reloadDatagrid();
            } else {
                Hint.show("Need to Sign In first");
                todo.doLogin();
            }
        })
    },

    initHint: function() {
        Hint.init({
            "selector": ".bb-alert"
        });
    },

    initTypeahead: function() {
        $('#search').typeahead({
            source: function(query, process) {
                todo.ajaxSuggest(query, process);
            }
        });
    },

    initDatagrid: function() {
        var dataSource = {
            columns: function () {
                return [
                    { property: 'priority', label: 'Priority', sortable: true },
                    { property: 'title', label: 'Title', sortable: false },
                    { property: 'date', label: 'Due Date', sortable: true },
                    { property: 'description', label: 'Description', sortable: false },
                    { property: 'user', label: 'User', sortable: false },
                    { property: 'completed', label: 'Completed', sortable: false}
                ];
            },
            data: function (options, callback) {
                setTimeout(function () {
                    var startIndex = options.pageIndex * options.pageSize;
                    var endIndex = startIndex + options.pageSize;

                    var params = {
                        'text': ((options.search) ? (options.search) : '') + '*',
                        'start': startIndex,
                        'size': options.pageSize,
                        'sort': (options.sortDirection) ? options.sortDirection : 'asc',
                        'sortBy': (options.sortProperty) ? options.sortProperty : 'date',
                        'token': todo.token
                    };

                    if (options.filter) {
                        if (options.filter.value == 'completed')
                            params.completed = true;
                        if (options.filter.value == 'notCompleted')
                            params.completed = false;
                    }

                    var data = todo.ajaxFilter(params);

                    var count = data.length;
                    var end = (endIndex > count) ? count : endIndex;
                    var pages = Math.ceil(count / options.pageSize);
                    var page = options.pageIndex + 1;
                    var start = startIndex + 1;

                    callback({ data: data, start: start, end: end, count: count, pages: pages, page: page });

                }, this._delay)
            }
        }

        $('#itemsGrid').datagrid({
            dataSource: dataSource,
            stretchHeight: true
        });

        $('#datagrid-reload').on('click', function () {
            todo.reloadDatagrid();
        });
    },

    reloadDatagrid: function() {
        $('#itemsGrid').datagrid('reload');
    },

    ajaxFilter: function(params) {
        var result = null;
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
                    items[key].user = value.user.fullname;
                });
                result = items;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
        return result;
    },

    ajaxLogin: function(nickname, password) {
        var token = null;
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
                token = data;
                $.cookie("session", data);
                $.cookie("user", nickname);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
        return token;
    },

    ajaxUser: function(nickname) {
        $.ajax({
            url: "rest/user/" + nickname,
            async: false,
            type: "GET",
            data: {
                'token': todo.token
            },
            accepts: {
                text: "application/json"
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (user) {
                $('#user').text(user.fullname);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
    },

    ajaxSuggest: function(query, callback) {
        $.ajax({
            url: "rest/item/suggest",
            async: false,
            type: "GET",
            data: {
                text: query,
                'token': todo.token
            },
            accepts: {
                text: "application/json"
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (suggest) {
                callback(suggest);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
    }


}