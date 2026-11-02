package com.faculdade.mavenproject1; // Mantenha o seu pacote original

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/cadastrar")
public class CadastroServlet extends HttpServlet {

    private static final String SUPABASE_URL = "https://qedagvoobinkmczgmeli.supabase.co/rest/v1/usuarios";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFlZGFndm9vYmlua21jemdtZWxpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk5MTQyODAsImV4cCI6MjEwNTQ5MDI4MH0.yILHoh9pK55W2wUBPHaFpEyJtuBHAylBhfuTGqT5pLw";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String login = request.getParameter("login");
        String senhaPura = request.getParameter("senha");

        if (login == null || login.trim().isEmpty() || senhaPura == null || senhaPura.trim().isEmpty()) {
            out.print("Erro: Preencha todos os campos.");
            return;
        }

        // 🔒 Criptografa a senha com SHA-256 antes de enviar ao Supabase
        String senhaHash = gerarHashSHA256(senhaPura);

        try {
            URL url = new URL(SUPABASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", SUPABASE_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=minimal");
            conn.setDoOutput(true);

            // Monta o JSON com a senha já criptografada
            String jsonInputString = String.format("{\"email\": \"%s\", \"senha\": \"%s\"}", login, senhaHash);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 201 || responseCode == 200) {
                out.print("Sucesso");
            } else {
                out.print("Erro no Supabase. Código: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("Erro interno no servidor: " + e.getMessage());
        }
    }

    // Método seguro nativo Java para gerar Hash SHA-256
    private String gerarHashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
}