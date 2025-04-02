package com.example.auto_ria.setup;

import org.springframework.stereotype.Service;

@Service
public class SystemStateService {
    private boolean systemReady = false;

    public boolean isSystemReady() {
        return systemReady;
    }

    public void setSystemReady(boolean ready) {
        this.systemReady = ready;
    }
}
