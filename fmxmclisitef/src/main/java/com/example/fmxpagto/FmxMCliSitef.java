package com.example.fmxpagto;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;

import com.artech.actions.ApiAction;
import com.artech.externalapi.ExternalApi;
import com.artech.externalapi.ExternalApiResult;

public class FmxMCliSitef extends ExternalApi {
	final static String NAME = "FmxMCliSitef";
	private final static String METHOD_GPOS700 = "Pagamento";
	private int RC_OCR_CAPTURE = 777;

	public FmxMCliSitef(ApiAction action) {
		super(action);
		addMethodHandler(METHOD_GPOS700, 9, mMethodPagamento);
	}

	private final IMethodInvoker mMethodPagamento = new IMethodInvoker() {
		@Override
		public @NonNull ExternalApiResult invoke(List<Object> parameters) {
			//Services.Messages.showMessage(getContext().getString(com.example.genexusmodule.R.string.hello_message));

			if(parameters.get(0).equals("")){

				return ExternalApiResult.success("ERRO: O IP deve ser informado!;");
			}
			Log.e("NOBRE", "Parametro1");
			if(parameters.get(1).equals("")){
				return ExternalApiResult.success("ERRO: O VALOR deve ser informado!;");
			}
			Log.e("NOBRE", "Parametro2");
			if(parameters.get(2).equals("")){
				return ExternalApiResult.success("ERRO: A DATA deve ser informada!;");
			}
			Log.e("NOBRE", "Parametro3");
			if(parameters.get(3).equals("")){
				return ExternalApiResult.success("ERRO: A HORA deve ser informada!;");
			}
			Log.e("NOBRE", "Parametro4");
			if(parameters.get(4).equals("")){
				return ExternalApiResult.success("ERRO: O USUARIO deve ser informado!;");
			}
			Log.e("NOBRE", "Parametro5");
			if(parameters.get(5).equals("")){
				return ExternalApiResult.success("ERRO: A EMPRESA deve ser informada!;");
			}
			Log.e("NOBRE", "Parametro6");
			if(parameters.get(6).equals("")){
				return ExternalApiResult.success("ERRO: O EQUIPAMENTO deve ser informado!;");
			}
			Log.e("NOBRE", "Parametro7");
			if(parameters.get(7).equals("")){
				return ExternalApiResult.success("ERRO: O CUPOM deve ser informado!;");
			}
			Log.e("NOBRE", "Parametro8");
			if(parameters.get(8).equals("")){
				return ExternalApiResult.success("ERRO: A FUNÇÃO deve ser informada!;");
			}
			Log.e("NOBRE", "Inicio PutExtra");

			Intent intent = new Intent(getActivity(), TransactionActivity.class);

			intent.putExtra("ip", (String) parameters.get(0));
			intent.putExtra("value", (String) parameters.get(1));
			intent.putExtra("date", (String) parameters.get(2));
			intent.putExtra("hour", (String) parameters.get(3));
			intent.putExtra("operator", (String) parameters.get(4));
			intent.putExtra("empresa", (String) parameters.get(5));
			intent.putExtra("equipamento", (String) parameters.get(6));
			intent.putExtra("cupom", (String) parameters.get(7));
			intent.putExtra("funcao", (String) parameters.get(8));
			Log.e("NOBRE", "Final PutExtra");


			startActivityForResult(intent, RC_OCR_CAPTURE);
			return ExternalApiResult.SUCCESS_WAIT;
		}
	};

	@Override
	public @NonNull ExternalApiResult afterActivityResult(int requestCode, int resultCode, Intent result, String method, List<Object> methodParameters)
	{
		String returnString = "Erro: Sem retorno";//String.valueOf(OPEN_REQUEST_FOLDER);
		Log.e("NOBRE", returnString);

		//		Log.d("NOBRE", globalmethod);
		if (resultCode == Activity.RESULT_OK)
		{
//			if (globalmethod.equalsIgnoreCase(METHOD_DRAWIMAGE)) {
				if (result != null) {
					String text = result.getStringExtra("retorno");
					Log.e("RETORNO", text);
					returnString = text;
				}
//			}
		}
		return ExternalApiResult.success(returnString);

	}
}
