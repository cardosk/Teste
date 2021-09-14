/*/
+---------------------------------------------------------------------------+
+ Funcao | CTBA120 | Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+-----------+----------+-------+-----------------------+------+-------------+
| Descricao | Cadastro de Criterios de Rateio Externo |
+-----------+---------------------------------------------------------------+
| Sintaxe | CTBA120() |
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
#INCLUDE "CTBA120.CH"
#INCLUDE "PROTHEUS.CH"
#INCLUDE "FONT.CH"
FUNCTION CTBA120()
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
PRIVATE aRotina := { { OemToAnsi(STR0001),"AxPesqui", 0 , 1},; //"Pesquisar"
{ OemToAnsi(STR0002),"Ctb120Cad", 0 , 2},; //"Visualizar"
{ OemToAnsi(STR0003),"Ctb120Cad", 0 , 3},; //"Incluir"
{ OemToAnsi(STR0004),"Ctb120Cad", 0 , 4},; //"Alterar"
{ OemToAnsi(STR0005),"Ctb120Cad", 0 , 5} } //"Excluir"
+----------------------------------------------------------------+
| Define o cabecalho da tela de atualizacoes |
+----------------------------------------------------------------+
Private cCadastro := OemToAnsi(STR0006) //"Criterios de Rateio
+----------------------------------------------------------------+
| Endereca funcao Mbrowse |
+----------------------------------------------------------------+
mBrowse( 6, 1,22,75,"CTJ" )
Return
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CTB120CAD| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Cadastro de Rateio Externo |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120Cad(ExpC1,ExpN1,ExpN2) |
+------------+-----------------------------------------------------------+
| Parametros | ExpC1 = Alias do arquivo |
| | ExpN1 = Numero do registro |
| | ExpN2 = Numero da opcao selecionada |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
/*/
Function Ctb120Cad(cAlias,nReg,nOpc)
Local aSaveArea := GetArea()
Local aCampos := {}
Local aAltera := {}
Local aTpSald := CTBCBOX("CTJ_TPSALD")
Local cArq
Local cRateio
Local cDescRat
lOCAL cMoedaLc
Local cTpSald
Local nOpca := 0
Local oGetDb
Local oDlg
Local oFnt
Local oTpSald
Private aTela := {}
Private aGets := {}
Private aHeader := {}
Private nTotalD := 0
Private nTotalC := 0
+----------------------------------------------------------------+
| Monta aHeader para uso com MSGETDB |
+----------------------------------------------------------------+
aCampos := Ctb120Head(@aAltera)
+----------------------------------------------------------------+
| Cria arquivo Temporario para uso com MSGETDB |
+----------------------------------------------------------------+
Ctb120Cri(aCampos,@cArq)
+----------------------------------------------------------------+
| Carrega dados para MSGETDB |
+----------------------------------------------------------------+
Ctb120Carr(nOpc)
If nOpc == 3 // Inclusao
cRateio := CriaVar("CTJ_RATEIO") // Numero do Rateio
cDescRat := CriaVar("CTJ_DESC") // Descricao do Rateio
cMoedaLC := CriaVar("CTJ_MOEDLC") // Moeda do Lancamento
cTpSald := CriaVar("CTJ_TPSALD") // Tipo do Saldo
Else // Visualizacao / Alteracao / Exclusao
cRateio := CTJ->CTJ_RATEIO
cDescRat := CTJ->CTJ_DESC
cMoedaLC := CTJ->CTJ_MOEDLC
cTpSald := CTJ->CTJ_TPSALD
EndIf
+----------------------------------------------------------------+
| Monta Tela Modelo 2 |
+----------------------------------------------------------------+
DEFINE MSDIALOG oDlg TITLE OemToAnsi(STR0006) From 9,0 To 32,80 OF oMainWnd //"Rateios
Externos"
DEFINE FONT oFnt NAME "Arial" Size 10,15
@ 18, 007 SAY OemToAnsi(STR0007) PIXEL //"Rateio: "
@ 18, 037 MSGET cRateio Picture "9999" SIZE 020,08 When (nOpc == 3);
Valid Ctb120Rat(cRateio) OF oDlg PIXEL
@ 18, 090 Say OemToAnsi(STR0008) PIXEL //"Descricao: "
@ 18, 120 MSGET cDescRat Picture "@!" SIZE 140,08 When (nOpc == 3 .Or. ;
nOpc == 4) Valid !Empty(cDescRat) OF oDlg PIXEL
@ 33, 007 Say OemToAnsi(STR0009) PIXEL // "Moeda:"
@ 32, 037 MSGET cMoedaLc Picture "@!" F3 "CTO" SIZE 020,08 When (nOpc == 3 .Or.;
nOpc == 4) Valid Ct120Moed(cMoedaLC) Of oDlg PIXEL
@ 33, 090 SAY OemToAnsi(STR0010) PIXEL // "Saldo:"
@ 32, 120 MSCOMBOBOX oTpSald VAR cTpSald ITEMS aTpSald When (nOpc == 3 .Or. ;
nOpc == 4) SIZE 45,08 OF oDlg PIXEL Valid (!Empty(cTpSald) .And.;
CtbTpSald(@cTpSald,aTpSald))
+----------------------------------------------------------------+
| Chamada da MSGETDB |
+----------------------------------------------------------------+
oGetDB := MSGetDB():New(044, 005, 120, 315, Iif(nOpc==3,4,nOpc),"CTB120LOK",;
"CTB120TOk", "+CTJ_SEQUEN",.t.,aAltera,,.t.,,"TMP")
+----------------------------------------------------------------+
| Validacao da janela |
+----------------------------------------------------------------+
ACTIVATE MSDIALOG oDlg ON INIT EnchoiceBar(oDlg,;
{||nOpca:=1,if(Ctb120TOK(),oDlg:End(),nOpca := 0)},;
{||nOpca:=2,oDlg:End()}) VALID nOpca != 0
IF nOpcA == 1 // Aceita operacao e grava dados
Begin Transaction
Ctb120Gra(cRateio,cDescRat,nOpc,cMoedaLC,cTpSald)
End Transaction
ENDIF
dbSelectArea(cAlias)
+----------------------------------------------------------------+
| Apaga arquivo temporario gerado para MSGETDB |
+----------------------------------------------------------------+
DbSelectArea( "TMP" )
DbCloseArea()
If Select("cArq") = 0
FErase(cArq+GetDBExtension())
EndIf
dbSelectArea("CTJ")
dbSetOrder(1)
Return nOpca
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CTB120RAT| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Verifica existencia do Rateio |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120Rat(ExpC1) |
+------------+-----------------------------------------------------------+
| Parametros | ExpC1 = Numero do Rateio |
+------------+-----------------------------------------------------------+
| Retorno | .T./.F. |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
/*/
Function Ctb120Rat(cRateio)
Local aSaveArea:= GetArea()
Local lRet := .T.
Local nReg
If Empty(cRateio)
lRet := .F.
Else
dbSelectArea("CTJ")
dbSetOrder(1)
nReg := Recno()
If dbSeek(xFilial()+cRateio)
Help(" ",1,"CTJNRATEIO")
lRet := .F.
EndIf
dbGoto(nReg)
EndIf
RestArea(aSaveArea)
Return lRet
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CTB120GRA| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Grava resgistro digitados |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120Gra(ExpC1,ExpC2,ExpN1,cExpC3,cExpC4) |
+------------+-----------------------------------------------------------+
| Parametros | ExpC1 = Numero do Rateio |
| | ExpC2 = Descricao do Rateio |
| | ExpN1 = Opcao do Menu (Inclusao / Alteracao etc) |
| | ExpC3 = Moeda do Rateio |
| | ExpC4 = Tipo de Saldo |
+------------+-----------------------------------------------------------+
| Retorno | Nenhum |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
Function Ctb120Gra(cRateio,cDescRat,nOpc,cMoedaLC,cTpSald)
Local aSaveArea := GetArea()
dbSelectArea("TMP")
dbgotop()
While !Eof()
If !TMP->CTJ_FLAG // Item nao deletado na MSGETDB
If nOpc == 3 .Or. nOpc == 4
dbSelectArea("CTJ")
dbSetOrder(1)
If !(dbSeek(xFilial()+cRateio+TMP->CTJ_SEQUEN))
RecLock( "CTJ", .t. )
CTJ->CTJ_FILIAL := xFilial()
CTJ->CTJ_RATEIO := cRateio
CTJ->CTJ_DESC := cDescRat
CTJ->CTJ_MOEDLC := cMoedaLC
CTJ->CTJ_TPSALD := cTpSald
Else
RecLock( "CTJ", .f. )
CTJ->CTJ_DESC := cDescRat
CTJ->CTJ_MOEDLC := cMoedaLC
CTJ->CTJ_TPSALD := cTpSald
Endif
For nCont := 1 To Len(aHeader)
If (aHeader[nCont][10] != "V" )
FieldPut(FieldPos(aHeader[nCont][2]),;
TMP->(FieldGet(FieldPos(aHeader[nCont][2]))))
EndIf
Next nCont
MsUnLock()
Elseif nOpc == 5 // Se for exclusao
dbSelectArea("CTJ")
dbSetOrder(1)
If dbSeek(xFilial()+cRateio+TMP->CTJ_SEQUEN)
RecLock("CTJ",.F.,.T.)
dbDelete()
MsUnlOCK()
EndIf
EndIf
Else // Item deletado na MSGETDB
dbSelectArea("CTJ")
dbSetOrder(1)
If dbSeek(xFilial()+cRateio+TMP->CTJ_SEQUEN)
RecLock( "CTJ", .f., .t. )
DbDelete()
MsUnlock()
Endif
EndIf
dbSelectArea("TMP")
dbSkip()
Enddo
RestArea(aSaveArea)
Return
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CTB120TOK| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Valida MSGETDB -> Tudo OK |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120TOK(ExpC1) |
+------------+-----------------------------------------------------------+
| Parametros | Nenhum |
+------------+-----------------------------------------------------------+
| Retorno | Nenhum |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
/*/
Function Ctb120TOk()
Local aSaveArea := GetArea()
Local lRet := .T.
Local nTotalD := 0
Local nTotalC := 0
dbSelectArea("TMP")
dbGotop()
While !Eof()
If !TMP->CTJ_FLAG
If !Ctb120LOK()
lRet := .F.
Exit
EndiF
If !Empty(TMP->CTJ_DEBITO)
nTotalD += TMP->CTJ_PERCEN
EndIf
If !Empty(TMP->CTJ_CREDITO)
nTotalC += TMP->CTJ_PERCEN
EndIf
EndIf
dbSkip()
EndDo
nTotalD := Round(nTotalD,2)
nTotalC := Round(nTotalC,2)
If lRet
IF (nTotalD > 0 .And. nTotalD != 100 ).Or. (nTotalC > 0 .And. nTotalC != 100)
Help(" ",1,"CTJ100%")
lRet := .F.
EndIF
EndIf
RestArea(aSaveArea)
Return lRet
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CTB120LOK| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Valida MSGETDB -> LinhaOK |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120LOK(ExpC1) |
+------------+-----------------------------------------------------------+
| Parametros | Nenhum |
+------------+-----------------------------------------------------------+
| Retorno | Nenhum |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
/*/
Function CTB120LOK()
Local lRet := .T.
Local nCont
If !TMP->CTJ_FLAG
If Empty(TMP->CTJ_PERCEN)
Help(" ",1,"CTJVLZERO")
lRet := .F.
EndIf
If lRet
ValidaConta(TMP->CTJ_DEBITO,"1",,,.t.)
EndIf
If lRet
ValidaConta(TMP->CTJ_CREDITO,"2",,,.T.)
EndIf
EndIf
Return lRet
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CTB120Cri| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Cria Arquivo Temporario para MSGETDB |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120Cri(ExpA1,ExpC1) |
+------------+-----------------------------------------------------------+
| Parametros | ExpA1 = Matriz com campos a serem criados |
| | ExpC1 = Nome do arquivo temporario |
+------------+-----------------------------------------------------------+
| Retorno | Nenhum |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
/*/
Function Ctb120Cria(aCampos,cArq)
Local cChave
Local aSaveArea := GetArea()
cChave := "CTJ_SEQUEN"
cArq := CriaTrab(aCampos,.t.)
dbUseArea(.t.,,cArq,"TMP",.f.,.f.)
RestArea(aSaveArea)
Return
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |CTB120Head| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+----------+-------+-----------------------+------+----------+
| Descricao | Montar aHeader para arquivo temporario da MSGETDB |
+------------+------------------------------------------------------------+
| Sintaxe | Ctb120Head(ExpA1) |
+------------+------------------------------------------------------------+
| Parametros | ExpA1 = Matriz com campos que podem ser alterados |
+------------+------------------------------------------------------------+
| Retorno | ExpA1 = Matriz com campos a serem criados no arq temporario|
+------------+------------------------------------------------------------+
| Uso | CTBA120 |
+------------+------------------------------------------------------------+
/*/
Function Ctb120Head(aAltera)
Local aSaveArea:= GetArea()
Local aFora := {"CTJ_RATEIO","CTJ_DESC","CTJ_MOEDLC","CTJ_TPSALD","CTJ_VALOR"}
Local aCampos := {}
Local nCriter := 0
PRIVATE nUsado := 0
// Montagem da matriz aHeader
dbSelectArea("SX3")
dbSetOrder(1)
dbSeek("CTJ")
While !EOF() .And. (x3_arquivo == "CTJ")
If Alltrim(x3_campo) == "CTJ_SEQUEN" .Or. ;
x3Uso(x3_usado) .and. cNivel >= x3_nivel
If Ascan(aFora,Trim(X3_CAMPO)) <= 0
nUsado++
AADD(aHeader,{ TRIM(X3Titulo()), x3_campo, x3_picture,;
x3_tamanho, x3_decimal, x3_valid,;
x3_usado, x3_tipo, "TMP", x3_context } )
If Alltrim(x3_campo) <> "CTJ_SEQUEN"
Aadd(aAltera,Trim(X3_CAMPO))
EndIf
EndIF
EndIF
aAdd( aCampos, { SX3->X3_CAMPO, SX3->X3_TIPO, SX3->X3_TAMANHO,;
SX3->X3_DECIMAL } )
dbSkip()
EndDO
Aadd(aCampos,{"CTJ_FLAG","L",1,0})
RestArea(aSaveArea)
Return aCampos
/*/
+------------+----------+-------+-----------------------+------+----------+
| Funcao |CTB120Carr| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+----------+-------+-----------------------+------+----------+
| Descricao | Carrega dados para MSGETDB |
+------------+------------------------------------------------------------+
| Sintaxe | Ctb120Carr(ExpN1) |
+------------+------------------------------------------------------------+
| Parametros | ExpN1 = Opcao do Menu -> Inclusao / Alteracao etc |
+------------+------------------------------------------------------------+
| Retorno | Nenhum |
+------------+------------------------------------------------------------+
| Uso | CTBA120 |
+------------+------------------------------------------------------------+
/*/
Function CTB120Carr(nOpc)
Local aSaveArea:= GetArea()
Local cAlias := "CTJ"
Local nPos
If nOpc != 3 // Visualizacao / Alteracao / Exclusao
cRateio := CTJ->CTJ_RATEIO
dbSelectArea("CTJ")
dbSetOrder(1)
If dbSeek(xFilial()+cRateio)
While !Eof() .And. CTJ->CTJ_FILIAL == xFilial() .And.;
CTJ->CTJ_RATEIO == cRateio
dbSelectArea("TMP")
dbAppend()
For nCont := 1 To Len(aHeader)
nPos := FieldPos(aHeader[nCont][2])
If (aHeader[nCont][08] <> "M" .And. aHeader[nCont][10] <> "V" )
FieldPut(nPos,(cAlias)-
>(FieldGet(FieldPos(aHeader[nCont][2]))))
EndIf
Next nCont
TMP->CTJ_FLAG := .F.
dbSelectArea("CTJ")
dbSkip()
EndDo
EndIf
Else
dbSelectArea("TMP")
dbAppend()
For nCont := 1 To Len(aHeader)
If (aHeader[nCont][08] <> "M" .And. aHeader[nCont][10] <> "V" )
nPos := FieldPos(aHeader[nCont][2])
FieldPut(nPos,CriaVar(aHeader[nCont][2],.T.))
EndIf
Next nCont
TMP->CTJ_FLAG := .F.
TMP->CTJ_SEQUEN:= "001"
EndIf
dbSelectArea("TMP")
dbGoTop()
RestArea(aSaveArea)
Return
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |CT120Moed| Autor | Pilar S. Albaladejo | Data | 24/07/00 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Valida Moeda do Lancamento |
+------------+-----------------------------------------------------------+
| Sintaxe | Ctb120Moed(ExpC1) |
+------------+-----------------------------------------------------------+
| Parametros | ExpC1 = Moeda a ser validada |
+------------+-----------------------------------------------------------+
| Retorno | .T./.F. |
+------------+-----------------------------------------------------------+
| Uso | CTBA120 |
+------------+-----------------------------------------------------------+
/*/
Function Ct120MoedLC(cMoeda)
Local aCtbMoeda:= {}
Local lRet := .T.
aCtbMoeda := CtbMoeda(cMoeda)
If Empty(aCtbMoeda[1])
Help(" ",1,"NOMOEDA")
lRet := .F.
Endif
Return lRet
