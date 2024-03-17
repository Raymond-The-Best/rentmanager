package com.epf.rentmanager.service;
import com.epf.rentmanager.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public interface ServiceTemplate<T> {
    public int delete(T myObject) throws ServiceException;
    public int create(T myObject) throws ServiceException;
    public List<T> findAll() throws ServiceException;
    public Optional<T> findById(int id) throws ServiceException;
    public String getServiceName();
}
