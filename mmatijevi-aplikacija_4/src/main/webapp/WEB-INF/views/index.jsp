<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Aplikacija 4</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
        crossorigin="anonymous"></script>
</head>

<body class="cover-container d-flex w-100 h-100 mx-auto flex-column">
    <nav class="navbar navbar-expand-lg navbar-light bg-light justify-content-around mb-5">
        <a class="navbar-brand m-l-2 ms-5" href="${pageContext.servletContext.contextPath}/mvc/pocetak">
            Aplikacija 4</a>
        <div class="collapse navbar-collapse" id="navbarText">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.servletContext.contextPath}/mvc/prijava/">Prijava</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"
                        href="${pageContext.servletContext.contextPath}/mvc/registracija/">Registracija</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.servletContext.contextPath}/mvc/korisnici/">Pregled
                        korisnika</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.servletContext.contextPath}/mvc/udaljenost/">Upravljanje
                        poslužiteljem udaljenosti</a>
                </li>
                
                <span style="position: fixed; right: 0">${korimePrijavljeni}</span>
            </ul>
        </div>
    </nav>
    <div class="text-center">
        <h2>Dobrodošli u aplikaciju 4!</h2>
    </div>
</body>

</html>