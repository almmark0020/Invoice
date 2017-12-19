/*
drop table FAC_CONFIGURACION;
drop table FAC_COMPANIA_APIKEY;
drop table FAC_PERIODO_EXTEMPORANEO;
drop table FAC_MOVIMIENTO_ERROR;
drop table FAC_MOVIMIENTO_FACTURACION;
drop table FAC_ORDEN_FACTURADA;
drop table FAC_ESTATUS_FACTURACION;
drop table FAC_USUARIO_BITACORA;
drop table FAC_USUARIO;
drop table FAC_ROL;
*/


CREATE TABLE FAC_COMPANIA_APIKEY
(
	ID_COMPANIA_APIKEY bigint IDENTITY NOT NULL,
	APIKEY varchar(128) NOT NULL,
	ACTIVO bit DEFAULT 'true' NOT NULL,
	COMPANIA_ID integer NOT NULL,
	FECHA_INICIO datetime NOT NULL,
	FECHA_FIN datetime NOT NULL,
	FECHA_REGISTRO datetime NOT NULL,
	ID_USUARIO_REGISTRO integer NOT NULL,
	constraint PK_FAC_COMPANIA_APIKEY primary key (ID_COMPANIA_APIKEY)
);


CREATE TABLE FAC_CONFIGURACION
(
	ID_CONFIGURACION int IDENTITY NOT NULL,
	CLAVE varchar(24) NOT NULL,
	VALOR varchar(256) NOT NULL,
	DESCRIPCION varchar(256) NOT NULL,
	constraint PK_FAC_CONFIGURACION primary key (ID_CONFIGURACION)
);


CREATE TABLE FAC_ESTATUS_FACTURACION
(
	ID_ESTATUS_FACTURACION int NOT NULL,
	DESCRIPCION varchar(24) NOT NULL,
	ACTIVO bit DEFAULT 'true' NOT NULL,
	constraint PK_FAC_ESTATUS_FACTURACION primary key (ID_ESTATUS_FACTURACION)
);


CREATE TABLE FAC_MOVIMIENTO_ERROR
(
	ID_MOVIMIENTO_ERROR bigint IDENTITY NOT NULL,
	ID_ORDEN_FACTURADA bigint NOT NULL,
	CODIGO_ERROR varchar(4) NOT NULL,
	MENSAJE_ERROR varchar(256) NOT NULL,
	FECHA_MOVIMIENTO datetime NOT NULL,
	ID_ESTATUS_FACTURACION int NOT NULL,
	REINTENTO bit DEFAULT 'false' NOT NULL,
	constraint PK_FAC_MOVIMIENTO_ERROR primary key (ID_MOVIMIENTO_ERROR)
);


CREATE TABLE FAC_MOVIMIENTO_FACTURACION
(
	ID_MOVIMIENTO_FACTURACION bigint IDENTITY NOT NULL,
	ID_ORDEN_FACTURADA bigint NOT NULL,
	ID_ESTATUS_FACTURACION int NOT NULL,
	UUID varchar(40) null,
	CFDI_XML varchar(128) null,
	CFDI_PDF varchar(128) null,
	FECHA_MOVIMIENTO datetime NOT NULL,
	constraint PK_FAC_MOVIMIENTO_FACTURACION primary key (ID_MOVIMIENTO_FACTURACION)
);


CREATE TABLE FAC_ORDEN_FACTURADA
(
	ID_ORDEN_FACTURADA bigint IDENTITY NOT NULL,
	ID_SINIESTRO int NOT NULL,
	FOLIO varchar(25) NOT NULL,
	TIPO_ORDEN char NOT NULL,
	CIA_DEUDORA int NOT NULL,
	CIA_ACREEDORA int NOT NULL,
	FECHA_ESTATUS_SIPAC datetime NOT NULL,
	ESTATUS_SIPAC varchar NOT NULL,
	SINIESTRO_ACREEDOR    varchar(20) null,
	SINIESTRO_DEUDOR    varchar(20) null,
	SINIESTRO_CORRECTO    varchar(20) null,
	ORIGEN        char(1)   null,
	POLIZA_ACREEDOR     varchar(20) null,
	POLIZA_DEUDOR     varchar(20) null,
	MONTO       money   null,   
	NOMBRE_CIA_ACREEDORA    varchar(18) null,
	RFC_ACREEDORA     varchar(14) null,
	RAZON_SOCIAL_ACREEDORA    varchar(100)  null,
	REGIMEN_FISCAL_ACREEDORA  varchar(4)  null,
	CP_ACREEDORA      varchar(6)  null,
	NOMBRE_CIA_DEUDORA    varchar(18) null,
	RFC_DEUDORA     varchar(14) null,
	RAZON_SOCIAL_DEUDORA    varchar(100)  null,
	REGIMEN_FISCAL_DEUDORA    varchar(4)  null,
	TIPO_CAPTURA char(1) null,
	FECHA_REGISTRO datetime  null,
	FECHA_ACEPTACION datetime null,
	FECHA_PAGO datetime null,
	COSTO money null,
	FECHA_EXPEDICION datetime null,
	FECHA_SINIESTRO datetime null,
	ESTADO int null,
	MUNICIPIO int null,
	SANCION money null, 
	CONTRAPARTE varchar(90),
	DIAS int null,
	MOTIVO int null,
	OBSERVACIONES_ACREEDOR varchar(255) null,
	OBSERVACIONES_DEUDOR varchar(255) null,
	OBSERVACIONES_COMITE varchar(255) null,
	CIRCUNSTANCIA_DEUDOR int null,
	CIRCUNSTANCIA_ACREEDOR int null,
	CAPTURADO varchar(255) null,
	MODIFICADO varchar(255) null,
	TIPO_TRANSPORTE_DEUDOR int null,
	TIPO_TRANSPORTE_ACREEDOR int null,
	FECHA_CONFIRMACION_PAGO datetime null,
	FECHA_PRIMER_RECHAZO datetime null,
	constraint PK_FAC_ORDEN_FACTURADA primary key (ID_ORDEN_FACTURADA)
);


CREATE TABLE FAC_PERIODO_EXTEMPORANEO
(
	ID_PERIODO_EXTEMPORANEO bigint IDENTITY NOT NULL,
	PERIODO int NOT NULL UNIQUE,
	NUMERO_DIAS int NOT NULL,
	ACTIVO bit DEFAULT 'true' NOT NULL,
	ID_USUARIO_REGISTRO int NOT NULL,
	FECHA_REGISTRO datetime NOT NULL,
	constraint PK_FAC_PERIODO_EXTEMPORANEO primary key (ID_PERIODO_EXTEMPORANEO)
);


CREATE TABLE FAC_ROL
(
	ID_ROL int IDENTITY NOT NULL,
	DESCRIPCION varchar(16) NOT NULL,
	ACTIVO bit DEFAULT 'true' NOT NULL,
	constraint PK_FAC_ROL primary key (ID_ROL)
);


CREATE TABLE FAC_USUARIO
(
	ID_USUARIO int IDENTITY NOT NULL,
	NOMBRE_USUARIO varchar(16) NOT NULL,
	PASSWORD varchar(32) NOT NULL,
	ID_ROL int NOT NULL,
	ID_COMPANIA int NULL,
	OFICINA_ID int NULL,
	CORREO_ELECTRONICO varchar(48) NOT NULL,
	ACTIVO bit DEFAULT 'true' NOT NULL,
	NOTIFICAR_EMAIL bit DEFAULT 'false' NOT NULL,
	NOMBRE varchar(25) NOT NULL,
	APELLIDO_PATERNO varchar(20) NOT NULL,
	APELLIDO_MATERNO varchar(20),
	TELEFONO varchar(25),
	constraint PK_FAC_USUARIO primary key (ID_USUARIO)
);

CREATE TABLE FAC_USUARIO_BITACORA
(
	ID_USUARIO_BITACORA int IDENTITY NOT NULL,
	ID_USUARIO int NOT NULL,
	NOMBRE_USUARIO varchar(16) NOT NULL,
	PASSWORD varchar(32) NOT NULL,
	ID_ROL int NOT NULL,
	ID_COMPANIA int NULL,
	OFICINA_ID int NULL,
	CORREO_ELECTRONICO varchar(48) NOT NULL,
	ACTIVO bit DEFAULT 'true' NOT NULL,
	NOTIFICAR_EMAIL bit DEFAULT 'false' NOT NULL,
	NOMBRE varchar(25) NOT NULL,
	APELLIDO_PATERNO varchar(20) NOT NULL,
	APELLIDO_MATERNO varchar(20),
	TELEFONO varchar(25),
	ID_USUARIO_REGISTRO int NOT NULL,
	FECHA_REGISTRO datetime NOT NULL,
	constraint PK_FAC_USUARIO primary key (ID_USUARIO_BITACORA)
);

create table FAC_ORDEN_A_FACTURAR (
  SINIESTRO_ID      int   not null,
  FOLIO_ORDEN     varchar(25) not null,
  ESTATUS_ID      char(1)   not null,
  FECHA_ESTATUS     datetime  not null,
  SINIESTRO_ACREEDOR    varchar(20) null,
  SINIESTRO_DEUDOR    varchar(20) null,
  SINIESTRO_CORRECTO    varchar(20) null,
  ORIGEN        char(1)   null,
  POLIZA_ACREEDOR     varchar(20) null,
  POLIZA_DEUDOR     varchar(20) null,
  MONTO       money   null,   
  TIPO_ORDEN      char(1)   null, 
  CIA_ACREEDORA         int   null,   
  NOMBRE_CIA_ACREEDORA    varchar(18) null,
  RFC_ACREEDORA     varchar(14) null,
  RAZON_SOCIAL_ACREEDORA    varchar(100)  null,
  REGIMEN_FISCAL_ACREEDORA  varchar(4)  null,
  CP_ACREEDORA      varchar(6)  null,
  CIA_DEUDORA     int   null,
  NOMBRE_CIA_DEUDORA    varchar(18) null,
  RFC_DEUDORA     varchar(14) null,
  RAZON_SOCIAL_DEUDORA    varchar(100)  null,
  REGIMEN_FISCAL_DEUDORA    varchar(4)  null,
  ID_ESTATUS_FACTURACION    int   null,
  TIPO_CAPTURA char(1) null,
  FECHA_REGISTRO datetime  null,
  FECHA_ACEPTACION datetime null,
  FECHA_PAGO datetime null,
  COSTO money null,
  FECHA_EXPEDICION datetime null,
  FECHA_SINIESTRO datetime null,
  ESTADO int null,
  MUNICIPIO int null,
  SANCION money null, 
  CONTRAPARTE varchar(90),
  DIAS int null,
  MOTIVO int null,
  OBSERVACIONES_ACREEDOR varchar(255) null,
  OBSERVACIONES_DEUDOR varchar(255) null,
  OBSERVACIONES_COMITE varchar(255) null,
  CIRCUNSTANCIA_DEUDOR int null,
  CIRCUNSTANCIA_ACREEDOR int null,
  CAPTURADO varchar(255) null,
  MODIFICADO varchar(255) null,
  TIPO_TRANSPORTE_DEUDOR int null,
  TIPO_TRANSPORTE_ACREEDOR int null,
  FECHA_CONFIRMACION_PAGO datetime null,
  FECHA_PRIMER_RECHAZO datetime null,
  constraint PK_FAC_ORDEN_A_FACTURAR primary key (SINIESTRO_ID, FOLIO_ORDEN, ESTATUS_ID, FECHA_ESTATUS)
);

/* Create Foreign Keys */

ALTER TABLE FAC_COMPANIA_APIKEY
ADD constraint FK_FAC_APIKEY_COMPANIA foreign key (COMPANIA_ID) references COMPANIAS (CIA_ID);

ALTER TABLE FAC_ORDEN_FACTURADA
ADD constraint FK_FAC_ORDEN_COMPANIAD foreign key (CIA_DEUDORA) references COMPANIAS (CIA_ID);

ALTER TABLE FAC_ORDEN_FACTURADA
ADD constraint FK_FAC_ORDEN_COMPANIAA foreign key (CIA_ACREEDORA) references COMPANIAS (CIA_ID);

ALTER TABLE FAC_MOVIMIENTO_ERROR
ADD constraint FK_FAC_MOV_ERROR_ESTATUS foreign key (ID_ESTATUS_FACTURACION) references FAC_ESTATUS_FACTURACION (ID_ESTATUS_FACTURACION);

ALTER TABLE FAC_MOVIMIENTO_FACTURACION
ADD constraint FK_FAC_MOV_FACT_ESTATUS foreign key (ID_ESTATUS_FACTURACION) references FAC_ESTATUS_FACTURACION (ID_ESTATUS_FACTURACION);

ALTER TABLE FAC_MOVIMIENTO_ERROR
ADD constraint FK_FAC_MOV_ERROR_ORDEN foreign key (ID_ORDEN_FACTURADA) references FAC_ORDEN_FACTURADA (ID_ORDEN_FACTURADA);

ALTER TABLE FAC_MOVIMIENTO_FACTURACION
ADD constraint FK_FAC_MOV_FACT_ORDEN foreign key (ID_ORDEN_FACTURADA) references FAC_ORDEN_FACTURADA (ID_ORDEN_FACTURADA);

ALTER TABLE FAC_USUARIO
ADD constraint FK_FAC_USUARIO_ROL foreign key (ID_ROL) references FAC_ROL (ID_ROL);

ALTER TABLE FAC_USUARIO
ADD constraint FK_FAC_USUARIO_OFICINA foreign key (ID_COMPANIA, OFICINA_ID) references OFICINAS (CIA_ID, OFICINA_ID);

ALTER TABLE FAC_USUARIO_BITACORA
ADD constraint FK_FAC_USUARIO_BIT foreign key (ID_USUARIO) references FAC_USUARIO (ID_USUARIO);

ALTER TABLE FAC_USUARIO_BITACORA
ADD constraint FK_FAC_USUARIO_BIT_USUARIO foreign key (ID_USUARIO_REGISTRO) references FAC_USUARIO (ID_USUARIO);

ALTER TABLE FAC_COMPANIA_APIKEY
ADD constraint FK_FAC_APIKEY_USUARIO foreign key (ID_USUARIO_REGISTRO) references USUARIOS (USUARIO_ID);

ALTER TABLE FAC_PERIODO_EXTEMPORANEO
ADD constraint FK_FAC_PERIODO_USUARIO foreign key (ID_USUARIO_REGISTRO) references USUARIOS (USUARIO_ID);

