async function loadChart1() {
    // set spinner while the chart loads
    $("#mostCoursesCategoriesSpinner").show();
    $("#mostCoursesCategoriesChart").hide();
    // get data for the most read genres chart (AJAX request and data from the database)

    let mostCoursesCathegoriesData = [];
    let mostCoursesCathegoriesLabels = [];

    $.ajax({
        url: "/mostCoursesCategories",
        method: "GET",
        success: function (response) {
            // hide spinner
            $("#mostCoursesCategoriesSpinner").hide();
            $("#mostCoursesCategoriesChart").show();
            // response is an array of objects (cathegory and number of courses) (get only the top 5)
            response = response.slice(0, 5);
            response.forEach((cathegory) => {
                mostCoursesCathegoriesData.push(cathegory[0])
                mostCoursesCathegoriesLabels.push(cathegory[1])
                }
            )

            // CHART 
            const ctx = document.getElementById('mostCoursesCategoriesChart');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: mostCoursesCathegoriesData,
                    datasets: [{
                        label: 'Número de cursos',
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

async function loadChart2() {
    // set spinner while the chart loads
    $("#mostInscribedCategoriesSpinner").show();
    $("#mostInscribedCategoriesChart").hide();
    // get data for the most read genres chart (AJAX request and data from the database)

    let mostInscribedCathegoriesData = [];
    let mostInscribedCathegoriesLabels = [];

    $.ajax({
        url: "/mostInscribedCategories",
        method: "GET",
        success: function (response) {
            // hide spinner
            $("#mostInscribedCategoriesSpinner").hide();
            $("#mostInscribedCategoriesChart").show();
            // response is an array of objects (cathegory and number of inscriptions) (get only the top 5)
            response = response.slice(0, 5);
            response.forEach((cathegory) => {
                mostInscribedCathegoriesData.push(cathegory[0])
                    mostInscribedCathegoriesLabels.push(cathegory[1])
                }
            )

            // CHART 
            const ctx = document.getElementById('mostInscribedCategoriesChart');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: mostInscribedCathegoriesData,
                    datasets: [{
                        label: 'Número de inscripciones',
                        data: mostInscribedCathegoriesLabels,
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
        $("#mostInscribedCategoriesSpinner").hide();
        $("#mostInscribedCategoriesChart").parent().append("<p>Ha habido un error al cargar el gráfico.</p>");

    })
}

loadChart1()
loadChart2()