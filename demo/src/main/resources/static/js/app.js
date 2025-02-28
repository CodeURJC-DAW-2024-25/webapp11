var currentPage = 0;

function editProfile() {
    document.getElementById("Settings").style.display = "block";  
}

function loadCourses() {
    $.ajax({
        url: '/getCourses',
        method: 'GET',
        data: {
            page: currentPage,
            pageSize: 10
        },
        success: function (htmlData) {
            var $htmlData = $(htmlData); // htmlData to jQuery

            if ($htmlData.length > 0) {
                $('#coursesContainer').append($htmlData);
                $('#spinner').hide();
                $('#loadMoreBtn').show();
                if ($htmlData.length < 19) {
                    $('#loadMoreBtn').hide();
                }
            }
        },
        error: function () {
            console.log('Error occurred while loading courses');
        }
    });
}

$('#loadMoreBtn').on('click', function () {
    $('#loadMoreBtn').hide();
    $('#spinner').show();
    currentPage++;
    loadCourses();
});

/*
$(document).ready(function () {
    $('#spinner').hide();
    loadCourses();
});*/

function loadCoursesByTopic() {
    $.ajax({
        url:'/getCoursesByTopic',
        method: 'GET',
        data: {
            page: currentPage,
            pageSize: 10,
            topic: decodeURIComponent(window.location.pathname.split("/").pop())
        },
        
        success: function (htmlData) {
            var $htmlData = $(htmlData); // htmlData to jQuery

            if ($htmlData.length > 0) {
                $('#coursesContainer').append($htmlData);
                $('#spinner').hide();
                $('#MoreBtn').show();
                if ($htmlData.length < 19) {
                    $('#MoreBtn').hide();
                }
            }
        },
        error: function () {
            console.log('Error occurred while loading courses');
        }
    });

}
$('#MoreBtn').on('click', function () {
    $('#MoreBtn').hide();
    $('#spinner').show();
    currentPage++;
    loadCoursesByTopic();
});


function loadTaughtCourses() {
    $.ajax({
        url: '/getTaughtCourses',
        method: 'GET',
        data: {
            page: currentPage,
            pageSize: 10
        },
        success: function (htmlData) {
            var $htmlData = $(htmlData); // htmlData to jQuery

            if ($htmlData.length > 0) {
                $('#my-courses-list').append($htmlData);
                $('#spinner').hide();
                $('#loadTaughtCourses').show();
                if ($htmlData.length < 10) {
                    $('#loadTaughtCourses').hide();
                }
            }
        },
        error: function () {
            console.log('Error occurred while loading courses');
        }
    });
}

$(document).ready(function () {
    if (window.location.pathname === "/profile") { 
        $('#spinner').hide();
        loadTaughtCourses();
    } else if (window.location.pathname === "/") { 
        $('#spinner').hide();
        loadCourses();
    } else if (window.location.pathname.startsWith("/showCourses/")) {
        $('#spinner').hide();
        loadCoursesByTopic();
    }
});

$('#loadTaughtCourses').on('click', function () {
    $('#loadTaughtCourses').hide();
    $('#spinner').show();
    currentPage++;
    loadTaughtCourses();
});