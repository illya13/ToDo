var todo = {
    token: null,
    totalCount: 0,

    init: function () {
        todo.initTypeahead();
        todo.initDatagrid();
        todo.initHint();
        todo.initLogout();
        todo.initLogin();
        todo.initNewItem();
        todo.initNewItemDlg();

        todo.token = $.cookie("session");
        if (todo.token) {
            todo.totalCount = todo.ajaxCount();
            todo.setUser($.cookie("user"));
        }
        else
            todo.doLogin();
    },

    doLogin: function() {
        $('#login').modal('show');
    },

    doNewItem: function() {
        $('#newItemDlg').modal('show');
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
                todo.totalCount = todo.ajaxCount();
                todo.reloadDatagrid();
            } else {
                Hint.show("Need to Sign In first");
                todo.doLogin();
            }
        })
    },

    initNewItem: function() {
        $("#newItem").on("click", function () {
            todo.doNewItem();
        });
    },

    initNewItemDlg: function() {
        $('#prioritySpinner').spinner('value', 1);

        $('#newItemDlg .input-append.date').datepicker({
            todayBtn: "linked",
            autoclose: true,
            todayHighlight: true
        });

        $('#newItemDlg').on('hidden.bs.modal', function () {
            var item = {
                priority: $('#prioritySpinner').spinner('value'),
                title: $('#title').val(),
                date: $('#newItemDlg .input-append.date').datepicker('getDate').getTime(),
                description: $('#description').val(),
                completed: false
            };
            todo.ajaxNewItem(item, $.cookie("user"));
            todo.totalCount = todo.ajaxCount();
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

                    var end = (endIndex > todo.totalCount) ?  todo.totalCount : endIndex;
                    var pages = Math.ceil( todo.totalCount / options.pageSize);
                    var page = options.pageIndex + 1;
                    var start = startIndex + 1;

                    callback({ data: data, start: start, end: end, count:  todo.totalCount, pages: pages, page: page });

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

    generateLinks: function(id, completed) {
        return '<a href="#" onClick="return todo.toggle(\''+id+'\');">' +
                ((completed) ? 'Yes' : 'No') +
            '</a>&nbsp;/&nbsp;<a href="#" onClick="return todo.delete(\''+id+'\');">Delete</a>';
    },

    delete: function(id) {
        todo.ajaxDelete(id);
        todo.totalCount = todo.ajaxCount();
        todo.reloadDatagrid();
    },

    toggle: function(id) {
        todo.ajaxToggle(id);
        todo.reloadDatagrid();
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
                    items[key].completed = todo.generateLinks(value.id, value.completed);
                    var date = new Date(value.date);
                    items[key].date =  date.getDate() + "/" + (date.getMonth()+1) + "/" +  + date.getFullYear();
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
                'text': query,
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
    },

    ajaxCount: function() {
        var count = 0;
        $.ajax({
            url: "rest/count/item",
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
            success: function (cnt) {
                count = cnt;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
        return count;
    },

    ajaxDelete: function(id) {
        var count = 0;
        $.ajax({
            url: "rest/item/"+id +"?token="+ todo.token,
            async: false,
            type: "DELETE",
            data: {},
            accepts: {
                text: "application/json"
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function () {
                Hint.show("deleted");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
        return count;
    },

    ajaxToggle: function(id) {
        var count = 0;
        $.ajax({
            url: "rest/item/"+id +"/toggle?token="+ todo.token,
            async: false,
            type: "POST",
            data: {
            },
            accepts: {
                text: "application/json"
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (item) {
                Hint.show((item.completed) ? "Completed" : "Uncompleted");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
        return count;
    },

    ajaxNewItem: function(item, nickname) {
        $.ajax({
            url: "rest/item?nickname="+ nickname + '&token='+ todo.token,
            async: false,
            type: "POST",
            data: JSON.stringify(item),
            accepts: {
                text: "application/json"
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (item) {
                todo.reloadDatagrid();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Hint.show(errorThrown);
            }
        });
    }
}