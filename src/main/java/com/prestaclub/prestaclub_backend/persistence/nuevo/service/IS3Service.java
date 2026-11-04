package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.io.IOException; 

public interface IS3Service {
    String uploadFile(byte[] fileBytes, String fileName) throws IOException;
}
