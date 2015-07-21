$(document).ready(function() {
    $("#menu-toggle").click(function() {
        if ($('#sidebar-wrapper').width() > 5) {
            $('#sidebar-wrapper').animate({'width': '5px'}, {duration: 500, queue: false});
            $('#sidebar').animate({'display': 'none'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'width': '100%'}, {duration: 500, queue: false});
            $('#menu-toggle').animate({'left': '0px'}, {duration: 500, queue: false});
        } else {
            $('#sidebar-wrapper').animate({'width': '25%'}, {duration: 500, queue: false});
            $('#sidebar').animate({'display': 'block'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'width': '75%'}, {duration: 500, queue: false});
            $('#menu-toggle').animate({'left': '25%'}, {duration: 500, queue: false});
        }
    });

    // TODO - this is broken now that we are using React - click handler needs to be in React
    $(".list-group-item").click(function() {
        console.log('clicked')
        var li = $(this).closest("li");
        if ($(li).hasClass("active")) {
            $(li).removeClass("active");
        } else {
            $(li).addClass("active");
        }
    });
})
