window.onload = function () {
    document.getElementById("submit").addEventListener("click",function () {
        var value = document.getElementById("urlinput").value;
        $.ajax({
            url: "/urlinput",
            type: "POST",
            data: {
                'value': value
            },
            success: function (data) {
                let response = JSON.parse(data);
                document.getElementById("download-link").setAttribute("href",response.download);
                document.getElementById("download-link").innerHTML = response.download;

            }
        });
    });
};