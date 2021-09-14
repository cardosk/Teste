#INCLUDE 'TOTVS.CH'

/*/{Protheus.doc} User Function HelloWorld
    (long_description)
    @type  Function
    @author user
    @since 25/08/2020
    @version version
    @param "HelloWorld", param_type, param_descr
    @return cRet, return_type, return_description
    @example
    (examples)
    @see (links_or_references)
    /*/

User Function Funcao()
    Local nTotal  := 10
    Local nConta  := 0

    For nConta := 1 to nTotal
      Alert(nConta)
    Next
Return
