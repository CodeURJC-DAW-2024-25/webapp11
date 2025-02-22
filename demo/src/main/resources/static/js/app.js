var currentPage = 0;
var total = 5;

function loadCourses() {
    $.ajax({
        url: '/getCourses',
        method: 'GET',
        data: {
            page: currentPage,
            pageSize: 4
        },
        success: function (htmlData) {
            var $htmlData = $(htmlData); // htmlData to jQuery

            if ($htmlData.length > 0) {
                $('#coursesContainer').append($htmlData);
                $('#loadMoreBtn').show();
                console.log($htmlData.length)
                if ($htmlData.length < 7) {
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
        currentPage++;
        loadCourses();
    });

    $(document).ready(function () {
        loadCourses();
    });