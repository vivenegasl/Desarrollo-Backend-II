package com.petsalud.backendpetsalud.entity;

/**
 * Enumeracion de roles para el control de acceso basado en roles (RBAC) en Clinica PetSalud.
 * Roles principales requeridos:
 * - CLIENTE: Propietarios de mascotas, reserva de citas, historial propio, compras y recompensas.
 * - EMPLEADO: Personal de atencion y veterinarios, gestion operativa, atencion medica e inventario.
 * - GERENTE: Administracion superior, reportes globales, gestion de usuarios y control total.
 *
 * Se mantienen VETERINARIO y ADMIN como compatibilidad con versiones previas de la plantilla.
 */
public enum Rol {
    CLIENTE,
    EMPLEADO,
    GERENTE,
    VETERINARIO, // Compatible con plantilla (sinonimo de EMPLEADO)
    ADMIN        // Compatible con plantilla (sinonimo de GERENTE)
}
