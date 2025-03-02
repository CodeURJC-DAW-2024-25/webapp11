async function loadCharts() {
    console.log("Ejecutando")
    // set spinner while the chart loads
    $("#mostInscribedCathegoriesSpinner").show();
    $("#mostInscribedCathegoriesChart").hide();
    // get data for the most read genres chart (AJAX request and data from the database)

    let mostInscribedCathegoriesData = [];
    let mostInscribedCathegoriesLabels = [];

    $.ajax({
        url: "/mostInscribedCathegories",
        method: "GET",
        success: function (response) {
            // hide spinner
            $("#mostInscribedCathegoriesSpinner").hide();
            $("#mostInscribedCathegoriesChart").show();
            // response is an array of objects (cathegory and number of inscriptions) (get only the top 5)
            response = response.slice(0, 5);
            response.forEach((cathegory) => {
                mostInscribedCathegoriesData.push(cathegory[0])
                    mostInscribedCathegoriesLabels.push(cathegory[1])
                }
            )

            // CHART 
            const ctx = document.getElementById('mostInscribedCathegoriesChart');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: mostInscribedCathegoriesData,
                    datasets: [{
                        label: 'Número de cursos',
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
        $("#mostInscribedCathegoriesSpinner").hide();
        $("#mostInscribedCathegoriesChart").parent().append("<p>Ha habido un error al cargar el gráfico.</p>");

    })
    /*
    $("#load-more-spinner").hide();
    $("#load-more-btn").parent().css({"display": "flex", "justify-content": "center", "text-align": "center"});
    $("#load-more-btn").css({
        "width": "85%",
        "position": "relative",
        "display": "flex",
        "justify-content": "center",
        "text-align": "center",
        "align-items": "center"
    });
    $("#load-more-btn i").css({"margin-right": "10px"});

    let currentPage = 1;
    let pageSize = 4;

    $("#load-more-btn").click(function () {
        // spinner animation to button while loading
        $("#load-more-label").hide();
        $("#load-more-spinner").show();
        // AJAX request
        $.ajax({
            type: "GET",
            url: "/landingPage/loadMore?page=" + currentPage + "&size=" + pageSize,
            contentType: "application/json",
            success: function (data) {
                if (data.trim() === "") {
                    $("#load-more-btn").hide();
                    $(".load-more-container").append("<p style='text-align: center;'>No hay nada m&aacutes que mostrar.<br>Sigue leyendo para obtener m&aacutes recomendaciones personalizadas.</p>");
                }
                currentPage++;
                $("#load-more-spinner").hide();
                $("#load-more-label").show();
                let posts = $(data).filter(".blog-post");
                let half = Math.ceil(posts.length / 2);
                let leftPosts = posts.slice(0, half);
                let rightPosts = posts.slice(half, posts.length);
                $("#left-post-column").append(leftPosts);
                $("#right-post-column").append(rightPosts);
            }
        });


    })
        */


}

loadCharts()