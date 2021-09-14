/*/
+---------------------------------------------------------------------------+
+ Funcao | FATA010 | Autor | Eduardo Riera | Data | 11/01/00 |
+-----------+----------+-------+-----------------------+------+-------------+
| Descricao | Cadastro de Processo de Vendas |
+-----------+---------------------------------------------------------------+
| Sintaxe | FATA010() |
+-----------+---------------------------------------------------------------+
| Uso | Generico |
+---------------------------------------------------------------------------+
| ATUALIZACOES SOFRIDAS DESDE A CONSTRUCAO NICIAL |
+-----------+--------+------+-----------------------------------------------+
|Programador| Data | BOPS | Motivo da Alteracao |
+-----------+--------+------+-----------------------------------------------+
| | | | |
+-----------+--------+------+-----------------------------------------------+
/*/
#INCLUDE "FATA010.CH"
#INCLUDE "FIVEWIN.CH"
#DEFINE APOS { 15, 1, 70, 315 }
Function Fata010()
/*/
+----------------------------------------------------------------+
| Define Array contendo as Rotinas a executar do programa +
| ----------- Elementos contidos por dimensao ------------ +
| 1. Nome a aparecer no cabecalho +
| 2. Nome da Rotina associada +
| 3. Usado pela rotina +
| 4. Tipo de Transacao a ser efetuada +
| 1 - Pesquisa e Posiciona em um Banco de Dados +
| 2 - Simplesmente Mostra os Campos +
| 3 - Inclui registros no Bancos de Dados +
| 4 - Altera o registro corrente +
| 5 - Remove o registro corrente do Banco de Dados +
+----------------------------------------------------------------+
/*/
PRIVATE cCadastro := OemToAnsi(STR0001) //"Processo de Venda"
PRIVATE aRotina := { { OemToAnsi(STR0002),"AxPesqui" ,0,1},; //"Pesquisar"
{ OemToAnsi(STR0003),"Ft010Visua",0,2},; //"Visual"
{ OemToAnsi(STR0004),"Ft010Inclu",0,3},; //"Incluir"
{ OemToAnsi(STR0005),"Ft010Alter",0,4},; //"Alterar"
{ OemToAnsi(STR0006),"Ft010Exclu",0,5} } //"Exclusao"
If !Empty( Select( "AC9" ) )
AAdd( aRotina, { STR0013,"MsDocument",0,4} )
EndIf
mBrowse( 6, 1,22,75,"AC1")
Return(.T.)
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010Visua| Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao de Tratamento da Visualizacao |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010Visua(ExpC1,ExpN2,ExpN3) |
+------------+------------------------------------------------------------+
| Parametros | ExpC1: Alias do arquivo |
| | ExpN2: Registro do Arquivo |
| | ExpN3: Opcao da MBrowse |
+------------+------------------------------------------------------------+
| Retorno | Nenhum |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Function Ft010Visua(cAlias,nReg,nOpcx)
Local aArea := GetArea()
Local oGetDad
Local oDlg
Local nUsado := 0
Local nCntFor := 0
Local nOpcA := 0
Local lContinua := .T.
Local lQuery := .F.
Local cCadastro := OemToAnsi(STR0001) //"Processo de Venda"
Local cQuery := ""
Local cTrab := "AC2"
Local bWhile := {|| .T. }
Local aObjects := {}
Local aPosObj := {}
Local aSizeAut := MsAdvSize()
PRIVATE aHEADER := {}
PRIVATE aCOLS := {}
PRIVATE aGETS := {}
PRIVATE aTELA := {}
+----------------------------------------------------------------+
| Montagem de Variaveis de Memoria |
+----------------------------------------------------------------+
dbSelectArea("AC1")
dbSetOrder(1)
For nCntFor := 1 To FCount()
M->&(FieldName(nCntFor)) := FieldGet(nCntFor)
Next nCntFor
+----------------------------------------------------------------+
| Montagem do aHeader |
+----------------------------------------------------------------+
dbSelectArea("SX3")
dbSetOrder(1)
dbSeek("AC2")
While ( !Eof() .And. SX3->X3_ARQUIVO == "AC2" )
If ( X3USO(SX3->X3_USADO) .And. cNivel >= SX3->X3_NIVEL )
nUsado++
Aadd(aHeader,{ TRIM(X3Titulo()),;
TRIM(SX3->X3_CAMPO),;
SX3->X3_PICTURE,;
SX3->X3_TAMANHO,;
SX3->X3_DECIMAL,;
SX3->X3_VALID,;
SX3->X3_USADO,;
SX3->X3_TIPO,;
SX3->X3_ARQUIVO,;
SX3->X3_CONTEXT } )
EndIf
dbSelectArea("SX3")
dbSkip()
EndDo
+----------------------------------------------------------------+
| Montagem do aCols |
+----------------------------------------------------------------+
dbSelectArea("AC2")
dbSetOrder(1)
#IFDEF TOP
If ( TcSrvType()!="AS/400" )
lQuery := .T.
cQuery := "SELECT *,R_E_C_N_O_ AC2RECNO "
cQuery += "FROM "+RetSqlName("AC2")+" AC2 "
cQuery += "WHERE AC2.AC2_FILIAL='"+xFilial("AC2")+"' AND "
cQuery += "AC2.AC2_PROVEN='"+AC1->AC1_PROVEN+"' AND "
cQuery += "AC2.D_E_L_E_T_<>'*' "
cQuery += "ORDER BY "+SqlOrder(AC2->(IndexKey()))
cQuery := ChangeQuery(cQuery)
cTrab := "FT010VIS"
dbUseArea(.T.,"TOPCONN",TcGenQry(,,cQuery),cTrab,.T.,.T.)
For nCntFor := 1 To Len(aHeader)
TcSetField(cTrab,AllTrim(aHeader[nCntFor][2]),aHeader[nCntFor,8],aHeader[nCnt
For,4],aHeader[nCntFor,5])
Next nCntFor
Else
#ENDIF
AC2->(dbSeek(xFilial("AC2")+AC1->AC1_PROVEN))
bWhile := {|| xFilial("AC2") == AC2->AC2_FILIAL .And.;
AC1->AC1_PROVEN == AC2->AC2_PROVEN }
#IFDEF TOP
EndIf
#ENDIF
While ( !Eof() .And. Eval(bWhile) )
aadd(aCOLS,Array(nUsado+1))
For nCntFor := 1 To nUsado
If ( aHeader[nCntFor][10] != "V" )
aCols[Len(aCols)][nCntFor] := FieldGet(FieldPos(aHeader[nCntFor][2]))
Else
If ( lQuery )
AC2->(dbGoto((cTrab)->AC2RECNO))
EndIf
aCols[Len(aCols)][nCntFor] := CriaVar(aHeader[nCntFor][2])
EndIf
Next nCntFor
aCOLS[Len(aCols)][Len(aHeader)+1] := .F.
dbSelectArea(cTrab)
dbSkip()
EndDo
If ( lQuery )
dbSelectArea(cTrab)
dbCloseArea()
dbSelectArea(cAlias)
EndIf
aObjects := {}
AAdd( aObjects, { 315, 50, .T., .T. } )
AAdd( aObjects, { 100, 100, .T., .T. } )
aInfo := { aSizeAut[ 1 ], aSizeAut[ 2 ], aSizeAut[ 3 ], aSizeAut[ 4 ], 3, 3 }
aPosObj := MsObjSize( aInfo, aObjects, .T. )
DEFINE MSDIALOG oDlg TITLE cCadastro From aSizeAut[7],00 To aSizeAut[6],aSizeAut[5] OF
oMainWnd PIXEL
EnChoice( cAlias ,nReg, nOpcx, , , , , aPosObj[1], , 3 )
oGetDad := MSGetDados():New (aPosObj[2,1], aPosObj[2,2], aPosObj[2,3], aPosObj[2,4],
nOpcx, "Ft010LinOk" ,"AllwaysTrue","",.F.)
ACTIVATE MSDIALOG oDlg ON INIT EnchoiceBar(oDlg,{||oDlg:End()},{||oDlg:End()})
RestArea(aArea)
Return(.T.)
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010Inclu|Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao de Tratamento da Inclusao |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010Inclu(ExpC1,ExpN2,ExpN3) |
+------------+------------------------------------------------------------+
| Parametros | ExpC1: Alias do arquivo |
| | ExpN2: Registro do Arquivo |
| | ExpN3: Opcao da MBrowse |
+------------+------------------------------------------------------------+
| Retorno | Nenhum |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Function Ft010Inclu(cAlias,nReg,nOpcx)
Local aArea := GetArea()
Local cCadastro := OemToAnsi(STR0001) //"Processo de Venda"
Local oGetDad
Local oDlg
Local nUsado := 0
Local nCntFor := 0
Local nOpcA := 0
Local aObjects := {}
Local aPosObj := {}
Local aSizeAut := MsAdvSize()
PRIVATE aHEADER := {}
PRIVATE aCOLS := {}
PRIVATE aGETS := {}
PRIVATE aTELA := {}
+----------------------------------------------------------------+
| Montagem das Variaveis de Memoria |
+----------------------------------------------------------------+
dbSelectArea("AC1")
dbSetOrder(1)
For nCntFor := 1 To FCount()
M->&(FieldName(nCntFor)) := CriaVar(FieldName(nCntFor))
Next nCntFor
+----------------------------------------------------------------+
| Montagem da aHeader |
+----------------------------------------------------------------+
dbSelectArea("SX3")
dbSetOrder(1)
dbSeek("AC2")
While ( !Eof() .And. SX3->X3_ARQUIVO == "AC2" )
If ( X3USO(SX3->X3_USADO) .And. cNivel >= SX3->X3_NIVEL )
nUsado++
Aadd(aHeader,{ TRIM(X3Titulo()),;
TRIM(SX3->X3_CAMPO),;
SX3->X3_PICTURE,;
SX3->X3_TAMANHO,;
SX3->X3_DECIMAL,;
SX3->X3_VALID,;
SX3->X3_USADO,;
SX3->X3_TIPO,;
SX3->X3_ARQUIVO,;
SX3->X3_CONTEXT } )
EndIf
dbSelectArea("SX3")
dbSkip()
EndDo
+----------------------------------------------------------------+
| Montagem da Acols |
+----------------------------------------------------------------+
aadd(aCOLS,Array(nUsado+1))
For nCntFor := 1 To nUsado
aCols[1][nCntFor] := CriaVar(aHeader[nCntFor][2])
Next nCntFor
aCOLS[1][Len(aHeader)+1] := .F.
aObjects := {}
AAdd( aObjects, { 315, 50, .T., .T. } )
AAdd( aObjects, { 100, 100, .T., .T. } )
aInfo := { aSizeAut[ 1 ], aSizeAut[ 2 ], aSizeAut[ 3 ], aSizeAut[ 4 ], 3, 3 }
aPosObj := MsObjSize( aInfo, aObjects, .T. )
DEFINE MSDIALOG oDlg TITLE cCadastro From aSizeAut[7],00 To aSizeAut[6],aSizeAut[5] OF
oMainWnd PIXEL
EnChoice( cAlias ,nReg, nOpcx, , , , , aPosObj[1], , 3 )
oGetDad := MSGetDados():New(aPosObj[2,1], aPosObj[2,2], aPosObj[2,3], aPosObj[2,4],
nOpcx, "Ft010LinOk", "Ft010TudOk","",.T.)
ACTIVATE MSDIALOG oDlg ;
ON INIT EnchoiceBar(oDlg, {||nOpcA:=If(oGetDad:TudoOk() .And. Obrigatorio(aGets,aTela),
1,0),If(nOpcA==1,oDlg:End(),Nil)},{||oDlg:End()})
If ( nOpcA == 1 )
Begin Transaction
Ft010Grv(1)
If ( __lSX8 )
ConfirmSX8()
EndIf
EvalTrigger()
End Transaction
Else
If ( __lSX8 )
RollBackSX8()
EndIf
EndIf
RestArea(aArea)
Return(.T.)
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010Alter| Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao de Tratamento da Alteracao |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010Alter(ExpC1,ExpN2,ExpN3) |
+------------+------------------------------------------------------------+
| Parametros | ExpC1: Alias do arquivo |
| | ExpN2: Registro do Arquivo |
| | ExpN3: Opcao da MBrowse |
+------------+------------------------------------------------------------+
| Retorno | Nenhum |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Function Ft010Alter(cAlias,nReg,nOpcx)
Local aArea := GetArea()
Local cCadastro := OemToAnsi(STR0001) //"Processo de Venda"
Local oGetDad
Local oDlg
Local nUsado := 0
Local nCntFor := 0
Local nOpcA := 0
Local lContinua := .T.
Local cQuery := ""
Local cTrab := "AC2"
Local bWhile := {|| .T. }
Local aObjects := {}
Local aPosObj := {}
Local aSizeAut := MsAdvSize()
PRIVATE aHEADER := {}
PRIVATE aCOLS := {}
PRIVATE aGETS := {}
PRIVATE aTELA := {}
+----------------------------------------------------------------+
| Montagem das Variaveis de Memoria |
+----------------------------------------------------------------+
dbSelectArea("AC1")
dbSetOrder(1)
lContinua := SoftLock("AC1")
If ( lContinua )
For nCntFor := 1 To FCount()
M->&(FieldName(nCntFor)) := FieldGet(nCntFor)
Next nCntFor
+----------------------------------------------------------------+
| Montagem da aHeader |
+----------------------------------------------------------------+
dbSelectArea("SX3")
dbSetOrder(1)
dbSeek("AC2")
While ( !Eof() .And. SX3->X3_ARQUIVO == "AC2" )
If ( X3USO(SX3->X3_USADO) .And. cNivel >= SX3->X3_NIVEL )
nUsado++
Aadd(aHeader,{ TRIM(X3Titulo()),;
TRIM(SX3->X3_CAMPO),;
SX3->X3_PICTURE,;
SX3->X3_TAMANHO,;
SX3->X3_DECIMAL,;
SX3->X3_VALID,;
SX3->X3_USADO,;
SX3->X3_TIPO,;
SX3->X3_ARQUIVO,;
SX3->X3_CONTEXT } )
EndIf
dbSelectArea("SX3")
dbSkip()
EndDo
+----------------------------------------------------------------+
| Montagem da aCols |
+----------------------------------------------------------------+
dbSelectArea("AC2")
dbSetOrder(1)
#IFDEF TOP
If ( TcSrvType()!="AS/400" )
lQuery := .T.
cQuery := "SELECT *,R_E_C_N_O_ AC2RECNO "
cQuery += "FROM "+RetSqlName("AC2")+" AC2 "
cQuery += "WHERE AC2.AC2_FILIAL='"+xFilial("AC2")+"' AND "
cQuery += "AC2.AC2_PROVEN='"+AC1->AC1_PROVEN+"' AND "
cQuery += "AC2.D_E_L_E_T_<>'*' "
cQuery += "ORDER BY "+SqlOrder(AC2->(IndexKey()))
cQuery := ChangeQuery(cQuery)
cTrab := "FT010VIS"
dbUseArea(.T.,"TOPCONN",TcGenQry(,,cQuery),cTrab,.T.,.T.)
For nCntFor := 1 To Len(aHeader)
TcSetField(cTrab,AllTrim(aHeader[nCntFor][2]),aHeader[nCntFor,8],;
Header[nCntFor,4],aHeader[nCntFor,5])
Next nCntFor
Else
#ENDIF
AC2->(dbSeek(xFilial("AC2")+AC1->AC1_PROVEN))
bWhile := {|| xFilial("AC2") == AC2->AC2_FILIAL .And.;
AC1->AC1_PROVEN == AC2->AC2_PROVEN }
#IFDEF TOP
EndIf
#ENDIF
While ( !Eof() .And. Eval(bWhile) )
aadd(aCOLS,Array(nUsado+1))
For nCntFor := 1 To nUsado
If ( aHeader[nCntFor][10] != "V" )
aCols[Len(aCols)][nCntFor] :=
FieldGet(FieldPos(aHeader[nCntFor][2]))
Else
If ( lQuery )
AC2->(dbGoto((cTrab)->AC2RECNO))
EndIf
aCols[Len(aCols)][nCntFor] := CriaVar(aHeader[nCntFor][2])
EndIf
Next nCntFor
aCOLS[Len(aCols)][Len(aHeader)+1] := .F.
dbSelectArea(cTrab)
dbSkip()
EndDo
If ( lQuery )
dbSelectArea(cTrab)
dbCloseArea()
dbSelectArea(cAlias)
EndIf
EndIf
If ( lContinua )
aObjects := {}
AAdd( aObjects, { 315, 50, .T., .T. } )
AAdd( aObjects, { 100, 100, .T., .T. } )
aInfo := { aSizeAut[ 1 ], aSizeAut[ 2 ], aSizeAut[ 3 ], aSizeAut[ 4 ], 3, 3 }
aPosObj := MsObjSize( aInfo, aObjects, .T. )
DEFINE MSDIALOG oDlg TITLE cCadastro From aSizeAut[7],00 To aSizeAut[6],aSizeAut[5]
;
OF MainWnd PIXEL
EnChoice( cAlias ,nReg, nOpcx, , , , , aPosObj[1], , 3 )
oGetDad :=
MSGetDados():New(aPosObj[2,1],aPosObj[2,2],aPosObj[2,3],aPosObj[2,4],nOpcx,;
"Ft010LinOk","Ft010TudOk","",.T.)
ACTIVATE MSDIALOG oDlg ;
ON INIT
EnchoiceBar(oDlg,{||nOpca:=If(oGetDad:TudoOk().And.Obrigatorio(aGets,aTela),1,0),;
If(nOpcA==1,oDlg:End(),Nil)},{||oDlg:End()})
If ( nOpcA == 1 )
Begin Transaction
Ft010Grv(2)
If ( __lSX8 )
ConfirmSX8()
EndIf
EvalTrigger()
End Transaction
Else
If ( __lSX8 )
RollBackSX8()
EndIf
EndIf
EndIf
Endif
RestArea(aArea)
Return(.T.)
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010Exclu| Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao de Tratamento da Exclusao |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010Exclu(ExpC1,ExpN2,ExpN3) |
+------------+------------------------------------------------------------+
| Parametros | ExpC1: Alias do arquivo |
| | ExpN2: Registro do Arquivo |
| | ExpN3: Opcao da MBrowse |
+------------+------------------------------------------------------------+
| Retorno | Nenhum |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Function Ft010Exclu(cAlias,nReg,nOpcx)
Local aArea := GetArea()
Local cCadastro := OemToAnsi(STR0001) //"Processo de Venda"
Local oGetDad
Local oDlg
Local nUsado := 0
Local nCntFor := 0
Local nOpcA := 0
Local lContinua := .T.
Local cQuery := ""
Local cTrab := "AC2"
Local bWhile := {|| .T. }
Local aObjects := {}
Local aPosObj := {}
Local aSizeAut := MsAdvSize()
PRIVATE aHEADER := {}
PRIVATE aCOLS := {}
PRIVATE aGETS := {}
PRIVATE aTELA := {}
+----------------------------------------------------------------+
| Montagem das Variaveis de Memoria |
+----------------------------------------------------------------+
dbSelectArea("AC1")
dbSetOrder(1)
lContinua := SoftLock("AC1")
If ( lContinua )
For nCntFor := 1 To FCount()
M->&(FieldName(nCntFor)) := FieldGet(nCntFor)
Next nCntFor
+----------------------------------------------------------------+
| Montagem da aHeader |
+----------------------------------------------------------------+
dbSelectArea("SX3")
dbSetOrder(1)
dbSeek("AC2")
While ( !Eof() .And. SX3->X3_ARQUIVO == "AC2" )
If ( X3USO(SX3->X3_USADO) .And. cNivel >= SX3->X3_NIVEL )
nUsado++
Aadd(aHeader,{ TRIM(X3Titulo()),;
TRIM(SX3->X3_CAMPO),;
SX3->X3_PICTURE,;
SX3->X3_TAMANHO,;
SX3->X3_DECIMAL,;
SX3->X3_VALID,;
SX3->X3_USADO,;
SX3->X3_TIPO,;
SX3->X3_ARQUIVO,;
SX3->X3_CONTEXT } )
EndIf
dbSelectArea("SX3")
dbSkip()
EndDo
+----------------------------------------------------------------+
| Montagek da aCols |
+----------------------------------------------------------------+
dbSelectArea("AC2")
dbSetOrder(1)
#IFDEF TOP
If ( TcSrvType()!="AS/400" )
lQuery := .T.
cQuery := "SELECT *,R_E_C_N_O_ AC2RECNO "
cQuery += "FROM "+RetSqlName("AC2")+" AC2 "
cQuery += "WHERE AC2.AC2_FILIAL='"+xFilial("AC2")+"' AND "
cQuery += "AC2.AC2_PROVEN='"+AC1->AC1_PROVEN+"' AND "
cQuery += "AC2.D_E_L_E_T_<>'*' "
cQuery += "ORDER BY "+SqlOrder(AC2->(IndexKey()))
cQuery := ChangeQuery(cQuery)
cTrab := "FT010VIS"
dbUseArea(.T.,"TOPCONN",TcGenQry(,,cQuery),cTrab,.T.,.T.)
For nCntFor := 1 To Len(aHeader)
TcSetField(cTrab,AllTrim(aHeader[nCntFor][2]),aHeader[nCntFor,8],;
aHeader[nCntFor,4],aHeader[nCntFor,5])
Next nCntFor
Else
#ENDIF
AC2->(dbSeek(xFilial("AC2")+AC1->AC1_PROVEN))
bWhile := {|| xFilial("AC2") == AC2->AC2_FILIAL .And.;
AC1->AC1_PROVEN == AC2->AC2_PROVEN }
#IFDEF TOP
EndIf
#ENDIF
While ( !Eof() .And. Eval(bWhile) )
aadd(aCOLS,Array(nUsado+1))
For nCntFor := 1 To nUsado
If ( aHeader[nCntFor][10] != "V" )
aCols[Len(aCols)][nCntFor] :=
FieldGet(FieldPos(aHeader[nCntFor][2]))
Else
If ( lQuery )
AC2->(dbGoto((cTrab)->AC2RECNO))
EndIf
aCols[Len(aCols)][nCntFor] := CriaVar(aHeader[nCntFor][2])
EndIf
Next nCntFor
aCOLS[Len(aCols)][Len(aHeader)+1] := .F.
dbSelectArea(cTrab)
dbSkip()
EndDo
If ( lQuery )
dbSelectArea(cTrab)
dbCloseArea()
dbSelectArea(cAlias)
EndIf
EndIf
If ( lContinua )
aObjects := {}
AAdd( aObjects, { 315, 50, .T., .T. } )
AAdd( aObjects, { 100, 100, .T., .T. } )
aInfo := { aSizeAut[ 1 ], aSizeAut[ 2 ], aSizeAut[ 3 ], aSizeAut[ 4 ], 3, 3 }
aPosObj := MsObjSize( aInfo, aObjects, .T. )
DEFINE MSDIALOG oDlg TITLE cCadastro From aSizeAut[7],00 To ;
aSizeAut[6],aSizeAut[5] OF oMainWnd PIXEL
EnChoice( cAlias ,nReg, nOpcx, , , , , aPosObj[1], , 3 )
oGetDad :=
MSGetDados():New(aPosObj[2,1],aPosObj[2,2],aPosObj[2,3],aPosObj[2,4],nOpcx,;
"Ft010LinOk","Ft010TudOk","",.F.)
ACTIVATE MSDIALOG oDlg ;
ON INIT
EnchoiceBar(oDlg,{||nOpca:=If(oGetDad:TudoOk(),1,0),If(nOpcA==1,oDlg:End(),Nil)},;
{||oDlg:End()})
If ( nOpcA == 1 )
Begin Transaction
If Ft010DelOk()
Ft010Grv(3)
EvalTrigger()
EndIf
End Transaction
EndIf
EndIf
RestArea(aArea)
Return(.T.)
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010LinOK| Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao de Validacao da linha OK |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010LinOk()
+------------+------------------------------------------------------------+
| Parametros | Nennhum |
+------------+------------------------------------------------------------+
| Retorno | Nenhum |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Function Ft010LinOk()
Local lRetorno:= .T.
Local nPStage := aScan(aHeader,{|x| AllTrim(x[2])=="AC2_STAGE"})
Local nPDescri:= aScan(aHeader,{|x| AllTrim(x[2])=="AC2_DESCRI"})
Local nCntFor := 0
Local nUsado := Len(aHeader)
If ( !aCols[n][nUsado+1] )
+----------------------------------------------------------------+
| Verifica os campos obrigatorios |
+----------------------------------------------------------------+
If ( nPStage == 0 .Or. nPDescri == 0 )
Help(" ",1,"OBRIGAT")
lRetorno := .F.
EndIf
If ( lRetorno .And. (Empty(aCols[n][nPStage]) .Or. Empty(aCols[n][nPDescri])))
Help(" ",1,"OBRIGAT")
lRetorno := .F.
EndIf
+----------------------------------------------------------------+
| Verifica se não há estagios repetidos |
+----------------------------------------------------------------+
If ( nPStage != 0 .And. lRetorno )
For nCntFor := 1 To Len(aCols)
If ( nCntFor != n .And. !aCols[nCntFor][nUsado+1])
If ( aCols[n][nPStage] == aCols[nCntFor][nPStage] )
Help(" ",1,"FT010LOK01")
lRetorno := .F.
EndIf
EndIf
Next nCntFor
EndIf
EndIf
Return(lRetorno)
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010Grv | Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao de Gravacao do Processe de Venda |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010Grv(ExpN1) |
+------------+------------------------------------------------------------+
| Parametros | ExpN1: Opcao do Menu (Inclusao / Alteracao / Exclusao) |
+------------+------------------------------------------------------------+
| Retorno | .T. |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Static Function Ft010Grv(nOpc)
Local aArea := GetArea()
Local aUsrMemo := If( ExistBlock( "FT010MEM" ), ExecBlock( "FT010MEM", .F.,.F. ), {} )
Local aMemoAC1 := {}
Local aMemoAC2 := {}
Local aRegistro := {}
Local cQuery := ""
Local lGravou := .F.
Local nCntFor := 0
Local nCntFor2 := 0
Local nUsado := Len(aHeader)
Local nPStage := aScan(aHeader,{|x| AllTrim(x[2])=="AC2_STAGE"})
Local nPMEMO := aScan(aHeader,{|x| AllTrim(x[2])=="AC2_MEMO"})
If ValType( aUsrMemo ) == "A" .And. Len( aUsrMemo ) > 0
For nLoop := 1 to Len( aUsrMemo )
If aUsrMemo[ nLoop, 1 ] == "AC1"
AAdd( aMemoAC1, { aUsrMemo[ nLoop, 2 ], aUsrMemo[ nLoop, 3 ] } )
ElseIf aUsrMemo[ nLoop, 1 ] == "AC2"
AAdd( aMemoAC2, { aUsrMemo[ nLoop, 2 ], aUsrMemo[ nLoop, 3 ] } )
EndIf
Next nLoop
EndIf
+----------------------------------------------------------------+
| Guarda os registros em um array para atualizacao |
+----------------------------------------------------------------+
dbSelectArea("AC2")
dbSetOrder(1)
#IFDEF TOP
If ( TcSrvType()!="AS/400" )
cQuery := "SELECT AC2.R_E_C_N_O_ AC2RECNO "
cQuery += "FROM "+RetSqlName("AC2")+" AC2 "
cQuery += "WHERE AC2.AC2_FILIAL='"+xFilial("AC2")+"' AND "
cQuery += "AC2.AC2_PROVEN='"+M->AC1_PROVEN+"' AND "
cQuery += "AC2.D_E_L_E_T_<>'*' "
cQuery += "ORDER BY "+SqlOrder(AC2->(IndexKey()))
cQuery := ChangeQuery(cQuery)
dbUseArea(.T.,"TOPCONN",TcGenQry(,,cQuery),"FT010GRV",.T.,.T.)
dbSelectArea("FT010GRV")
While ( !Eof() )
aadd(aRegistro,AC2RECNO)
dbSelectArea("FT010GRV")
dbSkip()
EndDo
dbSelectArea("FT010GRV")
dbCloseArea()
dbSelectArea("AC2")
Else
#ENDIF
dbSeek(xFilial("AC2")+M->AC1_PROVEN)
While ( !Eof() .And. xFilial("AC2") == AC2->AC2_FILIAL .And.;
M->AC1_PROVEN == AC2->AC2_PROVEN )
aadd(aRegistro,AC2->(RecNo()))
dbSelectArea("AC2")
dbSkip()
EndDo
#IFDEF TOP
EndIf
#ENDIF
Do Case
+----------------------------------------------------------------+
| Inclusao / Alteracao |
+----------------------------------------------------------------+
Case nOpc != 3
For nCntFor := 1 To Len(aCols)
If ( nCntFor > Len(aRegistro) )
If ( !aCols[nCntFor][nUsado+1] )
RecLock("AC2",.T.)
EndIf
Else
AC2->(dbGoto(aRegistro[nCntFor]))
RecLock("AC2")
EndIf
If ( !aCols[nCntFor][nUsado+1] )
lGravou := .T.
For nCntFor2 := 1 To nUsado
If ( aHeader[nCntFor2][10] != "V" )
FieldPut(FieldPos(aHeader[nCntFor2][2]),aCols[nCntFor][nCn
tFor2])
EndIf
Next nCntFor2
+----------------------------------------------------------------+
| Grava os campos obrigatórios
+----------------------------------------------------------------+
AC2->AC2_FILIAL := xFilial("AC2")
AC2->AC2_PROVEN := M->AC1_PROVEN
If ( nPMemo != 0 .And. !Empty(aCols[nCntFor][nPMemo]))
MSMM(AC2-
>AC2_CODMEM,,,aCols[nCntFor][nPMemo],1,,,"AC2","AC2_CODMEM")
EndIf
+----------------------------------------------------------------+
| Grava os campos memo de usuario |
+----------------------------------------------------------------+
For nLoop := 1 To Len( aMemoAC2 )
MSMM(AC2->(FieldGet(aMemoAC2[nLoop,1])),,, ;
DFieldGet( aMemoAC2[nLoop,2], nCntFor
),1,,,"AC2",aMemoAC2[nLoop,1])
Next nLoop
Else
If ( nCntFor <= Len(aRegistro) )
dbDelete()
MSMM(AC2->AC2_CODMEM,,,,2)
+----------------------------------------------------------------
+
| Exclui os campos memo de usuario
|
+----------------------------------------------------------------
+
For nLoop := 1 To Len( aMemoAC2 )
MSMM(aMemoAC2[nLoop,1],,,,2)
Next nLoop
EndIf
EndIf
MsUnLock()
Next nCntFor
+----------------------------------------------------------------+
| Exclusao |
+----------------------------------------------------------------+
OtherWise
For nCntFor := 1 To Len(aRegistro)
AC2->(dbGoto(aRegistro[nCntFor]))
RecLock("AC2")
dbDelete()
MsUnLock()
MSMM(AC2->AC2_CODMEM,,,,2)
Next nCntFor
If !Empty( Select( "AC9" ) )
+----------------------------------------------------------------+
| Exclui a amarracao de conhecimento |
+----------------------------------------------------------------+
MsDocument( "AC1", AC1->( Recno() ), 2, , 3 )
EndIf
EndCase
+----------------------------------------------------------------+
| Atualizacao do cabecalho |
+----------------------------------------------------------------+
dbSelectArea("AC1")
dbSetOrder(1)
If ( MsSeek(xFilial("AC1")+M->AC1_PROVEN) )
RecLock("AC1")
Else
If ( lGravou )
RecLock("AC1",.T.)
EndIf
EndIf
If ( !lGravou )
dbDelete()
MSMM(AC1->AC1_CODMEM,,,,2)
+----------------------------------------------------------------+
| Exclui os campos memo de usuario |
+----------------------------------------------------------------+
For nLoop := 1 To Len( aMemoAC1 )
MSMM( AC1->( FieldGet( aMemoAC1[ nLoop, 1 ] ) ),,,,2)
Next nLoop
Else
For nCntFor := 1 To AC1->(FCount())
If ( FieldName(nCntFor)!="AC1_FILIAL" )
FieldPut(nCntFor,M->&(FieldName(nCntFor)))
Else
AC1->AC1_FILIAL := xFilial("AC1")
EndIf
Next nCntFor
MSMM(AC1->AC1_CODMEM,,,M->AC1_MEMO,1,,,"AC1","AC1_CODMEM")
+----------------------------------------------------------------+
| Grava os campos memo de usuario |
+----------------------------------------------------------------+
For nLoop := 1 To Len( aMemoAC1 )
MSMM( AC1->( FieldGet( aMemoAC1[nLoop,1] ) ),,,;
M->&( aMemoAC1[nLoop,2] ),1,,,"AC1",aMemoAC1[nLoop,1])
Next nLoop
EndIf
MsUnLock()
+----------------------------------------------------------------+
| Restaura integridade da rotina |
+----------------------------------------------------------------+
RestArea(aArea)
Return( .T. )
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010TudOK| Autor |Eduardo Riera | Data |13.01.2000|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Funcao TudoOK |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010TudOK() |
+------------+------------------------------------------------------------+
| Parametros | Nenhum |
+------------+------------------------------------------------------------+
| Retorno | .T./.F. |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Function Ft010TudOk()
Local lRet := .T.
Local nPosRelev := GDFieldPos( "AC2_RELEVA" )
Local nPosStage := GDFieldPos( "AC2_STAGE" )
Local nLoop := 0
Local nTotal := 0
Local nPosDel := Len( aHeader ) + 1
If !Empty( AScan( aCols, { |x| x[nPosRelev] > 0 } ) )
For nLoop := 1 To Len( aCols )
If !aCols[ nLoop, nPosDel ]
nTotal += aCols[ nLoop, nPosRelev ]
Else
+----------------------------------------------------------------+
| Permite excluir apenas se não estiver em uso por oportunidade |
+----------------------------------------------------------------+
AD1->( dbSetOrder( 5 ) )
If AD1->( dbSeek( xFilial( "AD1" ) + M->AC1_PROVEN +
aCols[nLoop,nPosStage] ) )
Aviso( STR0007, STR0011 + AllTrim( aCols[nLoop,nPosStage] ) + ;
STR0012, { STR0009 }, 2 ) ;
// Atencao // "A etapa " // " nao pode ser excluida pois esta em
uso por uma ou mais // oportunidades !"
lRet := .F.
Exit
EndIf
EndIf
Next nLoop
If lRet
If nTotal <> 100
Aviso( STR0007, STR0008, ;
{ STR0009 }, 2 ) //"Atencao !"###"A soma dos valores de relevancia
deve ser igual a 100% //!"###"Fechar"
lRet := .F.
EndIf
EndIf
EndIf
Return( lRet )
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |Ft010DelOk| Autor |Sergio Silveira | Data |18.01.2001|
|------------+----------+-------+-----------------------+------+----------+
| Descricao |Validacao da Exclusao |
+------------+------------------------------------------------------------+
| Sintaxe | Ft010DelOk() |
+------------+------------------------------------------------------------+
| Parametros | Nenhum |
+------------+------------------------------------------------------------+
| Retorno | .T./.F. |
+------------+------------------------------------------------------------+
| Uso | FATA010 |
+------------+------------------------------------------------------------+
/*/
Static Function Ft010DelOk()
LOCAL lRet := .T.
AD1->( dbSetOrder( 5 ) )
If AD1->( dbSeek( xFilial( "AD1" ) + M->AC1_PROVEN ) )
lRet := .F.
Aviso( STR0007, STR0010, { STR0009 }, 2 ) // "Atencao"
// "Este processo de venda nao pode ser excluido pois esta sendo utilizado em uma
ou mais
// oportunidades !", "Fechar"
EndIf
Return( lRet )
