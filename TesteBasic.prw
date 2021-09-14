/*/
+---------------------------------------------------------------------------+
+ Funcao | FINA010 | Autor | Wagner Xavier | Data | 28/04/92 |
+-----------+----------+-------+-----------------------+------+-------------+
| Descricao | Programa de atualizacao de Naturezas |
+-----------+---------------------------------------------------------------+
| Sintaxe | FINA010() |
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


#INCLUDE "PROTHEUS.CH"
User FUNCTION FINA010()
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
PRIVATE aRotina := { { OemToAnsi("pesquisar") ,"AxPesqui", 0 , 1},; //"Pesquisar"
{ OemToAnsi("visualizar") ,"AxVisual", 0 , 2},; //"Visualizar"
{ OemToAnsi("incluir") ,"AxInclui", 0 , 3},; //"Incluir"
{ OemToAnsi("alterar") ,"AxAltera", 0 , 4},; //"Alterar"
{ OemToAnsi("excluir") ,"FA010Del", 0 , 5, 3} } //"Excluir"
/*+----------------------------------------------------------------+
| Define o cabecalho da tela de atualizacoes |
+----------------------------------------------------------------+*/
PRIVATE cCadastro := OemToAnsi(atualizacao) //"Atualizacao de Naturezas"
/*+----------------------------------------------------------------+
| Endereca funcao Mbrowse |
+----------------------------------------------------------------+*/
mBrowse( 6, 1,22,75,"SED")
Return
/*/
+------------+---------+-------+-----------------------+------+----------+
| Funcao |FA010DEL | Autor | Wagner Xavier | Data | 8/04/92 |
+------------+---------+-------+-----------------------+------+----------+
| Descricao | Programa de exclusao de Naturezas |
+------------+-----------------------------------------------------------+
| Sintaxe | A010Deleta(ExpC1,ExpN1,ExpN2) |
+------------+-----------------------------------------------------------+
| Parametros | ExpC1 = Alias do arquivo |
| | ExpN1 = Numero do registro |
| | ExpN2 = Numero da opcao selecionada |
+------------+-----------------------------------------------------------+
| Uso | FINA010 |
+------------+-----------------------------------------------------------+
/*/
User FUNCTION FA010DEL(cAlias,nReg,nOpc)
//Local aAC := { OemToAnsi("abandona"),OemToAnsi("confirma") } //"Abandona"###"Confirma"
Local bCampo
Local lDeleta := .T.
Local oDlg
Local nCont
Local nOpca
/*+----------------------------------------------------------------+
| Monta a entrada de dados do arquivo |
+----------------------------------------------------------------+*/
Private aTELA[0][0],aGETS[0]
/*+----------------------------------------------------------------+
| Verifica se o arquivo esta realmente vazio ou se esta |
| posicionado em outra filial |
+----------------------------------------------------------------+*/
If EOF() .or. SED->ED_FILIAL != xFilial("SED")
HELP(" " , 1 , "ARQVAZIO")
Return Nil
Endif
While .T.
/*+----------------------------------------------------------------+
| Envia para processamento dos Gets |
+----------------------------------------------------------------+*/
dbSelectArea( cAlias )
bCampo := {|nCPO| Field(nCPO) }
FOR nCont := 1 TO FCount()
M->&(EVAL(bCampo,nCont)) := FieldGet(nCont)
NEXT nCont
nOpca := 1
DEFINE MSDIALOG oDlg TITLE cCadastro FROM 9,0 TO 28,80 OF oMainWnd
EnChoice( cAlias, nReg, nOpc, ,"AC",OemToAnsi("exclusao") ) //"Quanto a exclusao?"
ACTIVATE MSDIALOG oDlg ON INIT EnchoiceBar(oDlg, {|| nOpca := 2,oDlg:End()},;
{|| nOpca := 1,oDlg:End()})
DbSelectArea(cAlias)
dbSelectArea(cAlias)
IF nOpcA == 2
/*+----------------------------------------------------------------+
| Antes de deletar, verificar se existe movimentacao |
+----------------------------------------------------------------+*/
dbSelectArea("SE1")
dbSetOrder(3)
IF (dbSeek(cFilial+SED->ED_CODIGO))
Help(" ",1,"A010NAODEL")
lDeleta := .F.
MsUnlock()
Else
dbSelectArea("SE2")
dbSetOrder(2)
IF (dbSeek(cFilial+SED->ED_CODIGO))
Help(" ",1,"A010NAODEL")
lDeleta := .F.
MsUnlock( )
Else
dbSelectArea("SE5")
dbSetOrder(4)
IF (dbSeek(cFilial+SED->ED_CODIGO))
Help(" ",1,"A010NAODEL")
lDeleta := .F.
MsUnlock( )
Endif
Endif
Endif
If lDeleta
/*+----------------------------------------------------------------+
| Inicio da Protecao via TTS |
+----------------------------------------------------------------+*/
BEGIN TRANSACTION
dbSelectArea(cAlias)
RecLock(cAlias,.F.,.T.)
dbDelete()
END TRANSACTION
/*+----------------------------------------------------------------+
| Final da protecao via TTS |
+----------------------------------------------------------------+*/
Endif
Else
MsUnlock( )
Endif
Exit
Enddo
dbSelectArea("SE1")
dbSetOrder(1)
dbSelectArea("SE2")
dbSetOrder(1)
dbSelectArea("SE5")
dbSetOrder(1)
dbSelectArea(cAlias)
RETURN
