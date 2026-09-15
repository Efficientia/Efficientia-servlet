package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.model.DonoFazendaModel;
import com.efficientia.efficientia.DAO.impl.DonoFazendaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name= "DonoFzendaServlet", value = "/donoFazenda")

public class DonoFazendaServlet extends HttpServlet {

    private DonoFazendaDAO dao = new DonoFazendaDAO();

    @Override
    public void init(){
        dao = new DonoFazendaDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException{

        List<DonoFazendaModel> donoFazendaModels = dao.listar();

        req.setAttribute("donoFazendaModels", donoFazendaModels);

        req.getRequestDispatcher("");

    }
}
