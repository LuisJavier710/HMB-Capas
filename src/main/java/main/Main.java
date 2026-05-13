package main;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * @author Luis Javier Malvaez Ramos
 */

@WebServlet(
    urlPatterns = "/hmb",
    initParams = {
        @WebInitParam(name = "almacenadas", value = "100")
    }
)

public class Main extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private int maximo;
    private Random rand;

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        maximo = Integer.parseInt(config.getInitParameter("almacenadas"));

        // Mejor aleatorio normal
        rand = new Random();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int hUsuario;
        int comidas = 0;

        PrintWriter salida;

        HttpSession sesion;

        // =========================
        // COOKIES
        // =========================

        Cookie galletas[];
        Cookie galleta;

        int intentos = 0;

        galletas = request.getCookies();

        if (galletas != null) {

            for (Cookie g : galletas) {

                if (g.getName().equalsIgnoreCase("intentos")) {

                    intentos = Integer.parseInt(g.getValue());
                }
            }
        }

        // aumentamos intentos
        intentos++;

        // actualizamos cookie
        galleta = new Cookie("intentos", intentos + "");

        response.addCookie(galleta);

        // =========================
        // SESIONES
        // =========================

        salida = response.getWriter();

        response.setContentType("text/html");

        sesion = request.getSession();

        Integer valorSesion = (Integer) sesion.getAttribute("hmb");

        if (valorSesion != null) {

            comidas = valorSesion;

        } else {

            comidas = rand.nextInt(this.maximo);

            sesion.setAttribute("hmb", comidas);
        }

        salida.println("<html>");
        salida.println("<head>");
        salida.println("<title>HMB</title>");
        salida.println("</head>");
        salida.println("<body>");

        // mostramos intentos guardados en cookie
        salida.println("<h3>Intentos: " + intentos + "</h3>");

        String parametro = request.getParameter("cb");

        if (parametro != null) {

            hUsuario = Integer.parseInt(parametro);

            if (hUsuario == comidas) {

                salida.println("<h1>Has ganado</h1>");

                comidas = rand.nextInt(this.maximo);

                // actualizamos sesión
                sesion.setAttribute("hmb", comidas);

            } else if (hUsuario < comidas) {

                salida.println("Comí de más");

            } else {

                salida.println("Faltaron");
            }

        } else {

            salida.println("No se recibió número");
        }

        salida.println("</body>");
        salida.println("</html>");

        salida.close();
    }
}