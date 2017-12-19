create trigger TRIGGER_FACTURACION_SIPAC 
on BIT_ORDENES_DANO_MATERIAL
for insert as 
if(select count(*) from inserted where inserted.ESTATUS_ID in('A','S','C','E','B','D','Q','K') AND inserted.ORIGEN != 'R' ) >0
insert into dbo.FAC_ORDEN_A_FACTURAR (SINIESTRO_ID, FOLIO_ORDEN, ESTATUS_ID, FECHA_ESTATUS,
    SINIESTRO_ACREEDOR, SINIESTRO_DEUDOR, SINIESTRO_CORRECTO, ORIGEN,
    POLIZA_ACREEDOR, POLIZA_DEUDOR, MONTO, TIPO_ORDEN, CIA_ACREEDORA, NOMBRE_CIA_ACREEDORA,
    RFC_ACREEDORA, RAZON_SOCIAL_ACREEDORA, REGIMEN_FISCAL_ACREEDORA, CP_ACREEDORA,
    CIA_DEUDORA, NOMBRE_CIA_DEUDORA, RFC_DEUDORA, RAZON_SOCIAL_DEUDORA, REGIMEN_FISCAL_DEUDORA,
    ID_ESTATUS_FACTURACION)
    select distinct
        inserted.SINIESTRO_ID,
        inserted.FOLIO_ORDEN,
        inserted.ESTATUS_ID,
        inserted.FECHA_ESTATUS,
        inserted.SINIESTRO_ACREEDOR,
        sin.SINIESTRO_DEUDOR,
        inserted.SINIESTRO_CORRECTO,
        inserted.ORIGEN,
        inserted.POLIZA_ACREEDOR,
        sin.POLIZA,
        ( -- Se obtiene el costo promedio como monto
            select VALOR 
            from CPS 
            where TIPO_TRANS_GM = mar.TIPO_TRANS_ID 
                and INICIO_VIGENCIA = ( 
                    select MAX(INICIO_VIGENCIA) 
                    from CPS 
                    where TIPO_TRANS_GM = mar.TIPO_TRANS_ID
                )
        ),
        'D', -- TIPO_ORDEN
        cia_acc.CIA_ID,
        cia_acc.NOMBRE_CORTO,
        cia_acc.RFC,
        cia_acc.RAZON_SOCIAL,
        cia_acc.REGIMEN_FISCAL,
        cia_acc.LUGAR_EXPEDICION_CP,
        cia_deu.CIA_ID,
        cia_deu.NOMBRE_CORTO,
        cia_deu.RFC,
        cia_deu.RAZON_SOCIAL,
        cia_deu.REGIMEN_FISCAL,
        1, --FAC_ESTATUS_FACTURACION - EN PROCESO
		inserted.TIPO_CAPTURA, --TIPO_CAPTURA
		bf.FECHA_CAPTURA, --FECHA_REGISTRO
		bf.FECHA_ACEPTACION, --FECHA_ACEPTACION
		bf.FECHA_PAGO, --FECHA_PAGO
		inserted.IMPORTE_SALDO, --COSTO
		inserted.FECHA_EXP, -- FECHA_EXPEDICION
		sin.FECHA_SINIESTRO, -- FECHA_SINIESTRO
		( select ESTADO_ID from MUNICIPIOS where MUNICIPIO_ID = sin.MUNICIPIO_ID ), -- ESTADO
		sin.MUNICIPIO_ID, -- MUNICIPIO
		( select SUM(IMPORTE_SANCION) from SANCIONES 
		  where SINIESTRO_ID = sin.SINIESTRO_ID and FOLIO_ORDEN = inserted.FOLIO_ORDEN and TIPO_ORDEN = 'D'), -- SANCION
		cia_deu.NOMBRE, -- CONTRAPARTE
		inserted.DIAS_TRANSC, -- DIAS
		inserted.MOTIVO_ID, -- MOTIVO
		inserted.OBSERVACIONES, -- OBSERVACIONES_ACREEDOR
		sin.OBSERVACIONES, -- OBSERVACIONES_DEUDOR
		inserted.OBS_COMITE, -- OBSERVACIONES_COMITE
		sin.CIRCUNSTANCIA_ID_DEU, -- CIRCUNSTANCIA_DEUDOR
		inserted.CIRCUNSTANCIA_ID_ACR, -- CIRCUNSTANCIA_ACREEDOR
		usr.NOMBRE || ' ' || usr.AP_PATERNO || ' ' || usr.AP_MATERNO, -- CAPTURADO
		usr.NOMBRE || ' ' || usr.AP_PATERNO || ' ' || usr.AP_MATERNO, -- MODIFICADO
		( select TIPO_TRANS_ID from MARCAS mr1
		  join TIPO_VEHICULO tv1 on tv1.MARCA_ID = mr1.MARCA_ID
		  where tv1.TIPO_ID = sin.TIPO_ID ), -- TIPO_TRANSPORTE_DEUDOR
		mar.TIPO_TRANS_ID, -- TIPO_TRANSPORTE_ACREEDOR
		inserted.FECHA_ESTATUS, -- FECHA_CONFIRMACION_PAGO
		bf.FECHA_PRIMER_RECHAZO -- FECHA_PRIMER_RECHAZO
    from inserted
    join BIT_SINIESTROS sin on sin.SINIESTRO_ID = inserted.SINIESTRO_ID
    join COMPANIAS cia_deu on cia_deu.CIA_ID = sin.CIA_DEUDORA
    join COMPANIAS cia_acc on cia_acc.CIA_ID = inserted.CIA_ACREEDORA
    join TIPO_VEHICULO tv on tv.TIPO_ID = inserted.TIPO_ID
    join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID
	join V_BIT_ORD_DM_FECHAS bf on bf.SINIESTRO_ID = sin.SINIESTRO_ID and bf.FOLIO_ORDEN = inserted.FOLIO_ORDEN
	left outer join USUARIOS usr on usr.USUARIO_ID = inserted.USUARIO_ID
    where inserted.ESTATUS_ID in('A','S','C','E','B','D','Q','K') AND inserted.ORIGEN != 'R'
else if(select count(*) FROM inserted where inserted.ESTATUS_ID in('Q') AND inserted.ORIGEN = 'R') > 0
-- Para notas de crédito se necesita obtener las órdenes orignales
insert into FAC_ORDEN_A_FACTURAR (SINIESTRO_ID, FOLIO_ORDEN, ESTATUS_ID, FECHA_ESTATUS,
    SINIESTRO_ACREEDOR, SINIESTRO_DEUDOR, SINIESTRO_CORRECTO, ORIGEN,
    POLIZA_ACREEDOR, POLIZA_DEUDOR, MONTO, TIPO_ORDEN, CIA_ACREEDORA, NOMBRE_CIA_ACREEDORA,
    RFC_ACREEDORA, RAZON_SOCIAL_ACREEDORA, REGIMEN_FISCAL_ACREEDORA, CP_ACREEDORA,
    CIA_DEUDORA, NOMBRE_CIA_DEUDORA, RFC_DEUDORA, RAZON_SOCIAL_DEUDORA, REGIMEN_FISCAL_DEUDORA,
    ID_ESTATUS_FACTURACION)
    select 
        origSin.SINIESTRO_ID,
        inserted.FOLIO_ORDEN,
        inserted.ESTATUS_ID,
        inserted.FECHA_ESTATUS,
        ord.SINIESTRO_ACREEDOR,
        origSin.SINIESTRO_DEUDOR,
        inserted.SINIESTRO_CORRECTO,
        inserted.ORIGEN,
        ord.POLIZA_ACREEDOR,
        origSin.POLIZA,
        ( -- Se obtiene el costo promedio como monto
            select VALOR 
            from CPS 
            where TIPO_TRANS_GM = mar.TIPO_TRANS_ID 
                and INICIO_VIGENCIA = ( 
                    select MAX(INICIO_VIGENCIA) 
                    from CPS 
                    where TIPO_TRANS_GM = mar.TIPO_TRANS_ID
                )
        ),
        'D', -- TIPO_ORDEN
        cia_acc.CIA_ID,
        cia_acc.NOMBRE_CORTO,
        cia_acc.RFC,
        cia_acc.RAZON_SOCIAL,
        cia_acc.REGIMEN_FISCAL,
        cia_acc.LUGAR_EXPEDICION_CP,
        cia_deu.CIA_ID,
        cia_deu.NOMBRE_CORTO,
        cia_deu.RFC,
        cia_deu.RAZON_SOCIAL,
        cia_deu.REGIMEN_FISCAL,
        1, --FAC_ESTATUS_FACTURACION - EN PROCESO
		inserted.TIPO_CAPTURA, --TIPO_CAPTURA
		bf.FECHA_CAPTURA, --FECHA_REGISTRO
		bf.FECHA_ACEPTACION, --FECHA_ACEPTACION
		bf.FECHA_PAGO, --FECHA_PAGO
		inserted.IMPORTE_SALDO, --COSTO
		inserted.FECHA_EXP, -- FECHA_EXPEDICION
		sin.FECHA_SINIESTRO, -- FECHA_SINIESTRO
		( select ESTADO_ID from MUNICIPIOS where MUNICIPIO_ID = sin.MUNICIPIO_ID ), -- ESTADO
		sin.MUNICIPIO_ID, -- MUNICIPIO
		( select SUM(IMPORTE_SANCION) from SANCIONES 
		  where SINIESTRO_ID = sin.SINIESTRO_ID and FOLIO_ORDEN = inserted.FOLIO_ORDEN and TIPO_ORDEN = 'D'), -- SANCION
		cia_deu.NOMBRE, -- CONTRAPARTE
		inserted.DIAS_TRANSC, -- DIAS
		inserted.MOTIVO_ID, -- MOTIVO
		inserted.OBSERVACIONES, -- OBSERVACIONES_ACREEDOR
		sin.OBSERVACIONES, -- OBSERVACIONES_DEUDOR
		inserted.OBS_COMITE, -- OBSERVACIONES_COMITE
		sin.CIRCUNSTANCIA_ID_DEU, -- CIRCUNSTANCIA_DEUDOR
		inserted.CIRCUNSTANCIA_ID_ACR, -- CIRCUNSTANCIA_ACREEDOR
		usr.NOMBRE || ' ' || usr.AP_PATERNO || ' ' || usr.AP_MATERNO, -- CAPTURADO
		usr.NOMBRE || ' ' || usr.AP_PATERNO || ' ' || usr.AP_MATERNO, -- MODIFICADO
		( select TIPO_TRANS_ID from MARCAS mr1
		  join TIPO_VEHICULO tv1 on tv1.MARCA_ID = mr1.MARCA_ID
		  where tv1.TIPO_ID = sin.TIPO_ID ), -- TIPO_TRANSPORTE_DEUDOR
		mar.TIPO_TRANS_ID, -- TIPO_TRANSPORTE_ACREEDOR
		inserted.FECHA_ESTATUS, -- FECHA_CONFIRMACION_PAGO
		bf.FECHA_PRIMER_RECHAZO -- FECHA_PRIMER_RECHAZO
    from inserted
    join SINIESTROS origSin on origSin.SINIESTRO_DEUDOR = inserted.SINIESTRO_ACREEDOR and origSin.CIA_DEUDORA = inserted.CIA_ACREEDORA
    join SINIESTROS sin on sin.SINIESTRO_ID = inserted.SINIESTRO_ID
    join COMPANIAS cia_deu on cia_deu.CIA_ID = inserted.CIA_ACREEDORA
    join COMPANIAS cia_acc on cia_acc.CIA_ID = sin.CIA_DEUDORA
    join ORDENES_DANO_MATERIAL ord on ord.SINIESTRO_ID = origSin.SINIESTRO_ID and ord.FOLIO_ORDEN = inserted.FOLIO_ORDEN
    join TIPO_VEHICULO tv on tv.TIPO_ID = ord.TIPO_ID
    join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID
	join V_BIT_ORD_DM_FECHAS bf on bf.SINIESTRO_ID = sin.SINIESTRO_ID and bf.FOLIO_ORDEN = inserted.FOLIO_ORDEN
	left outer join USUARIOS usr on usr.USUARIO_ID = inserted.USUARIO_ID
    where inserted.ESTATUS_ID in('Q') AND inserted.ORIGEN = 'R'