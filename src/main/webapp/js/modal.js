$(function() {
    var modals = {};

    $(document).on("click", "a[data-bb]", function(e) {
        e.preventDefault();
        var type = $(this).data("bb");

        if (typeof modals[type] === 'function') {
            modals[type]();
        }
    });

    // let's namespace the demo methods; it makes them easier
    // to invoke
    modals.alert = function() {
        bootbox.alert("Hello world!");
    };

    modals.alert_callback = function() {
        bootbox.alert("Hello world!", function() {
            Hint.show("Hello world callback");
        });
    };

    modals.confirm = function() {
        bootbox.confirm("Are you sure?", function(result) {
            Hint.show("Confirm result: "+result);
        });
    };

    modals.alert_button = function() {
        bootbox.alert("This alert has custom button text", "So it does!");
    };

    modals.confirm_buttons = function() {
        bootbox.confirm("This confirm has custom buttons - see?", "No", "Yes!", function(result) {
            if (result) {
                Hint.show("Well done!");
            } else {
                Hint.show("Oh no - try again!");
            }
        });
    };

    modals.prompt = function() {
        bootbox.prompt("What is your name?", function(result) {
            if (result === null) {
                Hint.show("Prompt dismissed");
            } else {
                Hint.show("Hi <b>"+result+"</b>");
            }
        });
    };

    modals.dialog = function() {
        bootbox.dialog({
            message: "I am a custom dialog",
            title: "Custom title",
            buttons: {
                success: {
                    label: "Success!",
                    className: "btn-success",
                    callback: function() {
                        Hint.show("great success");
                    }
                },
                danger: {
                    label: "Danger!",
                    className: "btn-danger",
                    callback: function() {
                        Hint.show("uh oh, look out!");
                    }
                },
                main: {
                    label: "Click ME!",
                    className: "btn-primary",
                    callback: function() {
                        Hint.show("Primary button");
                    }
                }
            }
        });
    };
});