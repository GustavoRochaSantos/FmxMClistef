package com.example.fmxpagto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genexusmodule.R;

import br.com.softwareexpress.sitef.android.CliSiTef;
import br.com.softwareexpress.sitef.android.ICliSiTefListener;
import wangpos.sdk4.libbasebinder.Printer;

public class TransactionActivity extends Activity implements ICliSiTefListener {
    private static final int CAMPO_COMPROVANTE_CLIENTE = 121;
    private static final int CAMPO_COMPROVANTE_ESTAB = 122;
    private static final int CAMPO_COD_AUTORIZACAO_CREDITO = 135;
    private static final int CAMPO_CARTAO_INICIO_NUM = 136;
    private static final int CAMPO_COD_REDE_AUTORIZADORA = 158;
    private static final int CAMPO_CARTAO_VALIDADE = 1002;
    private static final int CAMPO_NOME_PORTADOR = 1003;
    private static final int CAMPO_CARTAO_FINAL_NUM = 1190;
    private static final int CAMPO_CARTAO_MASCARADO = 2021;
    private static final int CAMPO_COLETOU_SENHA_PINPAD = 5074;


    // Genexus Parameters
    private String ip;
    private String value;
    private String date;
    private String hour;
    private String operator;
    private String cupom;

    private String empresa;
    private String equipamento;

    private class RequestCode {
        private static final int GET_DATA = 1;
        private static final int END_STAGE_1_MSG = 2;
        private static final int END_STAGE_2_MSG = 3;
    }

    // Variaveis estaticas para nao serem reinicializadas ao rodar o display
    // Para tanto, vamos assumir que esta atividade nunca será executada em
    // paralelo com outra igual (singleton)
    private Printer mPrinter;
    private Boolean mPrintedAlready = false;
    private String  mPrintedData = "";

    private String aditionalData = "";
    private String impressao="";

    private int trnResultCode;
    private static String title;
    private static CliSiTef clisitef;
    private static TransactionActivity instance = null;
    private static int vReturn = 0;
    private static int funcao = 0;

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String data = " sdfa iosdfn iasdf oidas f=========== REIMPRESSAO ================= miopsda nmfimasd fasdmfa";
        String newData =data.replaceAll("", "");


        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_transaction);

        new Thread(){
            @Override
            public void run() {
                mPrinter = new Printer(getApplicationContext());
            }
        }.start();

        if(getIntent().getStringExtra("funcao").equals("")){
            returnData("ERRO: A FUNÇÃO deve ser informada!;");
        }

        ip = getIntent().getStringExtra("ip");
        value = getIntent().getStringExtra("value");
        date = getIntent().getStringExtra("date");
        hour = getIntent().getStringExtra("hour");
        operator = getIntent().getStringExtra("operator");
        empresa = getIntent().getStringExtra("empresa");
        equipamento = getIntent().getStringExtra("equipamento");
        cupom = getIntent().getStringExtra("cupom");
        funcao = Integer.parseInt(getIntent().getStringExtra("funcao"));

        //TODO - verificar se é para remover este botão - não está sendo utilizado
        Button btn = (Button) findViewById(R.id.btCfgCancela);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("NOBRE", "Cancelando");
                clisitef.abortTransaction(-1);
                returnData("ERRO: Cancelado pelo usuário;");

            }
        });

        instance = this;

        if (clisitef == null) {
            try {
                clisitef = new CliSiTef(this.getApplicationContext());
            } catch (Exception e) {
                //alert(e.getMessage());
            }
        }

        clisitef.setDebug(true);
        //int S = clisitef.configure("192.168.0.8", "00000000", "AA123456", "[TipoPinPad=ANDROID_AUTO;1=31406434895111;]");
        int S = clisitef.configure(ip, empresa, equipamento, "[TipoPinPad=ANDROID_AUTO;]");

        if (S == 0) {
            Log.d("LOG","Configurado");
        }

        clisitef.setMessageHandler(hndMessage);
        trnResultCode = -1; // undefined
        title = "";
        setStatus("");
        clisitef.setActivity(this);

        //vReturn = clisitef.startTransaction (this, 0, "1222,00", "123456", "23/03/2021", "11:10", "GUSTAVO", "[]");
        vReturn = clisitef.startTransaction (this, funcao, value, cupom, date, hour, operator, "[]");
        if (funcao==114) {
            btn.setVisibility(View.GONE);
            setStatus("Imprimindo...");
        }

        Log.e("LOG:", "Transaction: " + vReturn);
    }

    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    private void setStatus(String s) {
        ((TextView) findViewById(R.id.tvStatusTrn)).setText(s);
    }

    private void alert(String message) {
        Toast.makeText(TransactionActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void onData(int stage, int command, int fieldId, int minLength, int maxLength, byte[] input) {
        String data = "";
        Button btn = (Button) findViewById(R.id.btCfgCancela);
        setProgressBarIndeterminateVisibility(false);

        if (stage == 1) {
            // Evento onData recebido em uma startTransaction
        } else if (stage == 2) {
            // Evento onData recebido em uma finishTransaction
        }

        String mensagem = clisitef.getBuffer();

        Log.e("LOG:", " fieldId: " + fieldId + " stage: " + stage + " command: "+ command + "ondata: " + clisitef.getBuffer());

        switch (command) {
            case CliSiTef.CMD_GET_PINPAD_CONFIRMATION:
                break;
            case CliSiTef.CMD_RESULT_DATA:
                switch (fieldId) {
                    case CAMPO_COMPROVANTE_CLIENTE:
                        btn.setVisibility(View.GONE);
                        setStatus("Imprimindo...");
                        impressao = clisitef.getBuffer();
                        if (funcao==114) {
                            impressao = "Via Cliente" + System.getProperty("line.separator") +  impressao;
                        }
                        printResult(impressao);
                        break;
                    case CAMPO_COMPROVANTE_ESTAB:
                        btn.setVisibility(View.GONE);
                        setStatus("Imprimindo...");
                        impressao = clisitef.getBuffer();
                        if (funcao==114) {
                            impressao = "Via Cliente" + System.getProperty("line.separator") +  impressao;
                        }
                        Log.e("IMPRESSAO", impressao);
                        printResult(impressao);
                        break;

                    case  CAMPO_COD_AUTORIZACAO_CREDITO:
                        aditionalData += "COD_AUTORIZACAO_CREDITO:" + mensagem + ";";
                        break;
                    case  CAMPO_CARTAO_INICIO_NUM:
                        aditionalData += "CARTAO_INICIO_NUM:" + mensagem + ";";
                        break;
                    case  CAMPO_COD_REDE_AUTORIZADORA:
                        aditionalData += "COD_REDE_AUTORIZADORA:" + mensagem + ";";
                        break;
                    case  CAMPO_CARTAO_VALIDADE:
                        aditionalData += "CARTAO_VALIDADE:" + mensagem + ";";
                        break;
                    case  CAMPO_NOME_PORTADOR:
                        aditionalData += "NOME_PORTADOR:" + mensagem + ";";
                        break;
                    case  CAMPO_CARTAO_FINAL_NUM:
                        aditionalData += "CARTAO_FINAL_NUM:" + mensagem + ";";
                        break;
                    case  CAMPO_CARTAO_MASCARADO:
                        aditionalData += "CARTAO_MASCARADO:" + mensagem + ";";
                        break;
                    case  CAMPO_COLETOU_SENHA_PINPAD:
                        switch (mensagem.trim()){
                            case "1":
                                returnData("ERRO: Senha não informada, operação cancelada");
                                clisitef.abortTransaction(-1);
                                return;
                            case "0":
                                btn.setVisibility(View.GONE);
                                break;
                        }

                        break;
                }
                break;
            case CliSiTef.CMD_SHOW_MSG_CASHIER:
            case CliSiTef.CMD_SHOW_MSG_CUSTOMER:
            case CliSiTef.CMD_SHOW_MSG_CASHIER_CUSTOMER:
                setStatus(clisitef.getBuffer());
                break;
            case CliSiTef.CMD_SHOW_MENU_TITLE:
            case CliSiTef.CMD_SHOW_HEADER:
                title = clisitef.getBuffer();
                break;
            case CliSiTef.CMD_CLEAR_MSG_CASHIER:
            case CliSiTef.CMD_CLEAR_MSG_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MSG_CASHIER_CUSTOMER:
            case CliSiTef.CMD_CLEAR_MENU_TITLE:
            case CliSiTef.CMD_CLEAR_HEADER:
                title = "";
                setStatus("");
                break;
            case CliSiTef.CMD_CONFIRM_GO_BACK:
            case CliSiTef.CMD_CONFIRMATION: {
                switch(mensagem.trim()){
                    case "Conf.reimpressao":
                        clisitef.continueTransaction("0");
                        break;
                }

                return;
            }
            case CliSiTef.CMD_GET_FIELD_CURRENCY:
            case CliSiTef.CMD_GET_FIELD_BARCODE:
            case CliSiTef.CMD_GET_FIELD: {
                switch(mensagem.trim()){
                    case "Forneca o codigo do supervisor":
                        clisitef.continueTransaction("1");
                        break;
                }

                return;
            }
            case CliSiTef.CMD_GET_MENU_OPTION: {


                switch (mensagem.trim()){
                    case "1:CREDITO;2:PRIVATE LABEL;3:PRIVATE LABEL;":
                        clisitef.continueTransaction("1");
                        return;
                    case "1:Cheque;2:Cartao de Debito;3:Cartao de Credito;4:Cartao Private Label;5:Confirmacao de Pre-autorizacao;":
                        clisitef.continueTransaction("3");
                        return;
                }


            }
            case CliSiTef.CMD_PRESS_ANY_KEY: {
                switch (mensagem.trim()){
                    case "42 - Erro Pinpad":
                        returnData("ERRO: " + mensagem);
                        return;
                    case "43 - Cartao Removido":
                        returnData("ERRO: " + mensagem);
                        return;
                    case "12 - Erro Pinpad":
                        returnData("ERRO: " + mensagem);
                        return;
                    case "13 - Operacao Cancelada":
                        returnData("ERRO: " + mensagem);
                        return;
                }
                clisitef.continueTransaction("1");
                return;
            }
            case CliSiTef.CMD_ABORT_REQUEST:
                break;

            case 50:
                break;

            case 51:
                break;
            default:
                break;
        }

        setProgressBarIndeterminateVisibility(true);
        clisitef.continueTransaction(data);

    }

    private String getMessageDescription(int stage, int sts) {
        switch (sts) {
            case -1:
                return "ERRO:" + getString(R.string.msgModuloNaoConfigurado);
            case -2:
                return("ERRO:" + getString(R.string.msgCanceladoOperador));
            case -3:
                return( "ERRO:" + getString(R.string.msgFuncaoInvalida));
            case -4:
                return( "ERRO:" + getString(R.string.msgFaltaMemoria));
            case -5:
                return( "ERRO:" + getString(R.string.msgFalhaComunicacao));
            case -6:
                return( "ERRO:" + getString(R.string.msgCanceladoPortador));
            case -40:
                return( "ERRO:" + getString(R.string.msgNegadaSiTef));
            case -43:
                return( "ERRO: Generic"); //getString(R.string.msgErroPinPad);
            case -100:
                return( "ERRO:" + getString(R.string.msgOutrosErros));
            default:
                return( "ERRO:" + "Stage " + stage + getString(R.string.msgReturned) + " " + sts);
        }
    }

    public void onTransactionResult(int stage, int resultCode) {
        setProgressBarIndeterminateVisibility(false);
        trnResultCode = resultCode;
        //alert ("Fim do estágio " + stage + ", retorno " + resultCode);
        if (stage == 1 && resultCode == 0) { // Confirm the transaction
            try {
                Log.e("FinalMessage", mPrintedData);
                if (mPrintedData.length() > 0) {
                    String newReturn = "SUCESSO: " + mPrintedData + ";DATA_ADICIONAL " + aditionalData;
                    Log.e("FinalMessage", newReturn);
                    returnData( newReturn );
                } else {
                    returnData(  "ERRO: Sem dados de retorno;");
                }
                clisitef.finishTransaction(1);
            } catch (Exception e) {
                //alert(e.getMessage());
            }
        } else {
            if (resultCode == 0) {
                finish();
            } else {

                returnData(getMessageDescription(stage, resultCode));
                /*
                Intent i = new Intent(getApplicationContext(), MessageActivity.class);

                i.putExtra("message", getMessageDescription(stage, resultCode));
                startActivityForResult(i, stage == 1 ? RequestCode.END_STAGE_1_MSG : RequestCode.END_STAGE_2_MSG);

                 */
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.GET_DATA) {
            if (resultCode == RESULT_OK) {
                String in = "";
                if (data.getExtras() != null) {
                    in = data.getExtras().getString("input");
                }
                clisitef.continueTransaction(in);
            } else if (resultCode == RESULT_CANCELED) {
                clisitef.abortTransaction(-1);
            }
        } else if (requestCode == RequestCode.END_STAGE_1_MSG && trnResultCode != 0) {
            finish();
        } else if (requestCode == RequestCode.END_STAGE_2_MSG) {
            finish();
        }
    }

    private void setMessageTitle(int what) {
        setTitle(getString(what));
    }

    private static Handler hndMessage = new Handler() {
        public void handleMessage(android.os.Message message) {

            Log.e("LOG:", "Handler: " + message);
            switch (message.what) {
                case CliSiTef.EVT_BEGIN_PP_CONNECT:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setMessageTitle(R.string.msgPPSearching);
                    break;
                case CliSiTef.EVT_END_PP_CONNECT:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setTitle(R.string.app_name);
                    break;
                case CliSiTef.EVT_BEGIN_PP_CONFIG:
                    instance.setProgressBarIndeterminateVisibility(true);
                    instance.setMessageTitle(R.string.msgPPConfiguring);
                    break;
                case CliSiTef.EVT_END_PP_CONFIG:
                    instance.setProgressBarIndeterminateVisibility(false);
                    instance.setMessageTitle(R.string.msgPPConfigured);
                    break;
                case CliSiTef.EVT_BT_PP_DISCONNECT:
//                    instance.setProgressBarIndeterminateVisibility(false);
  //                  instance.setMessageTitle(R.string.msgPPDisconnected);
                    break;
            }
        }
    };

    private void printResult(String data){
        if(mPrintedAlready == false) {
            //init
            try {
                mPrinter.printInit();
                //output
                Log.e("Impressao", data);
                mPrinter.printString(data, 25, Printer.Align.CENTER, false, false);
                mPrinter.printPaper(60);
                mPrinter.printFinish();

                mPrintedData = data;
                mPrintedAlready = true;

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void returnData(String errorMsg){

        Intent data = new Intent();
        data.putExtra("retorno", errorMsg);
        setResult(Activity.RESULT_OK, data);

        Log.e("FinalMessage", mPrintedData);
        clisitef.abortTransaction(-1);
        finish();

    }

}

