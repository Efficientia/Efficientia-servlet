package com.efficientia.efficientia.DAO.impl;

import com.efficientia.efficientia.DAO.interfaces.GenericDAO;
import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.Model;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDAOImpl<T extends Model> implements GenericDAO<T> {

    protected Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }
}