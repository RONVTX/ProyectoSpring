package com.example.ProyectoSpring.config;

import org.hibernate.envers.RevisionListener;

/**
 * Listener para registrar información en cada revisión
 */
public class RevisionListenerConfig implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionAuditEntity entity = (RevisionAuditEntity) revisionEntity;
        // Aquí podríamos capturar el usuario actual y su IP
        entity.setUsuarioCambio("SISTEMA");
        entity.setIpAddress("127.0.0.1");
    }
}

