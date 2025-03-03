async function loadChart1() {
    // set spinner while the chart loads
    $("#mostCoursesCategoriesSpinner").show();
    $("#mostCoursesCategoriesChart").hide();
    // get data for the most read genres chart (AJAX request and data from the database)

    let mostCoursesCathegoriesData = [];
    let mostCoursesCathegoriesLabels = [];

    course_id = document.getElementById("course_id").value

    $.ajax({
        url: `/puntuationChart/${course_id}`,
        method: "GET",
        success: function (response) {
            // hide spinner
            $("#mostCoursesCategoriesSpinner").hide();
            $("#mostCoursesCategoriesChart").show();
            // response is an array of objects (cathegory and number of courses) (get only the top 5)
            console.log(response)
            response.forEach((cathegory) => {
                mostCoursesCathegoriesData.push(cathegory[0])
                mostCoursesCathegoriesLabels.push(cathegory[1])
                }
            )

            // CHART 
            const ctx = document.getElementById('mostValuedChart');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: mostCoursesCathegoriesData,
                    datasets: [{
                        label: 'Valoración',
                        data: mostCoursesCathegoriesLabels,
                        backgroundColor: '#519E8A',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function (value, index, values) {
                                    return Math.round(value);
                                }
                            },
                            grid: {
                                display: false
                            }
                        },
                        x: {
                            grid: {
                                display: false
                            }
                        }
                    }
                }
            });
        } // end of success function
        // if the request fails, append a text to the container
    }).fail(() => {
        $("#mostCoursesCategoriesSpinner").hide();
        $("#mostCoursesCategoriesChart").parent().append("<p>Ha habido un error al cargar el gráfico.</p>");

    })
}

loadChart1()
