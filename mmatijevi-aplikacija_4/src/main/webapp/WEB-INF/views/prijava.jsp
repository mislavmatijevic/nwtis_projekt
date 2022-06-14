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
            <a class="navbar-brand m-l-2" href="${pageContext.servletContext.contextPath}/mvc/prijava/pocetak">
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
                </ul>
            </div>
        </nav>
        <div class="text-center">
            <form method="POST">
                <div class="form-outline mb-4">
                    <input name="korime" type="text" id="form2Example1" class="form-control" />
                    <label class="form-label" for="form2Example1">Korisničko ime</label>
                </div>

                <div class="form-outline mb-4">
                    <input name="lozinka" type="password" id="form2Example2" class="form-control" />
                    <label class="form-label" for="form2Example2">Lozinka</label>
                </div>

                <button type="button" class="btn btn-primary btn-block mb-4">Prijava</button>

                <div class="text-center">
                    <p>Niste korisnik? <a
                            href="${pageContext.servletContext.contextPath}/mvc/registracija/">Registracija</a></p>
                </div>
            </form>
        </div>
    </body>

    </html>