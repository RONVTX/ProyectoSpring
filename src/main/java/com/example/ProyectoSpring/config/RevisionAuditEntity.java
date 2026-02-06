package com.example.ProyectoSpring.config;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import jakarta.persistence.*;

/**
 * Entidad de Revisión para Hibernate Envers
 * Registra información sobre cada cambio auditado
 */
@Entity
@Table(name = "revisiones")
@RevisionEntity(RevisionListenerConfig.class)
public class RevisionAuditEntity {

    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revisionId;

    @RevisionTimestamp
    private Long revisionTimestamp;

    @Column(name = "usuario_cambio")
    private String usuarioCambio;

    @Column(name = "ip_address")
    private String ipAddress;

    public RevisionAuditEntity() {
    }

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Long getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(Long revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    public String getUsuarioCambio() {
        return usuarioCambio;
    }

    public void setUsuarioCambio(String usuarioCambio) {
        this.usuarioCambio = usuarioCambio;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
