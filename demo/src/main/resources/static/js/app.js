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
            pageSize: 4
        },
        success: function (htmlData) {
            var $htmlData = $(htmlData); // htmlData to jQuery

            if ($htmlData.length > 0) {
                $('#coursesContainer').append($htmlData);
                $('#spinner').hide();
                $('#loadMoreBtn').show();
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
        $('#spinner').show();
        currentPage++;
        loadCourses();
    });

    $(document).ready(function () {
        $('#spinner').hide();
        loadCourses();
    });