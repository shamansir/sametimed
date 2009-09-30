package name.shamansir.sametimed.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import name.shamansir.sametimed.client.proto.IWavesClientViewService;
import name.shamansir.sametimed.client.proto.IWavesClientViewServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SametimedView implements EntryPoint {
	
	private final String ADD_CLIENT_FORM_ID = "add-client-form";
	private final String VIEWS_CONTAINER_ID = "client-views";
	private final String ERROR_BOX_ID       = "error"; 
	
	private final String CLIENT_RENDER_JS_FUNC = "renderClient";
	
	/**
	 * Create a remote service proxy to talk to the server-side Client View service.
	 */
	private final IWavesClientViewServiceAsync clientViewService = GWT
			.create(IWavesClientViewService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		final Button addViewButton = new Button("Add client");
		final TextBox usernameField = new TextBox();
		final RootPanel errorBox = RootPanel.get(ERROR_BOX_ID);
		
		// errorBox.removeStyleName("have-errors");
		errorBox.setStylePrimaryName("no-errors");
		
		usernameField.setText("username");
		addViewButton.addStyleName("addButton");

		RootPanel.get(ADD_CLIENT_FORM_ID).add(usernameField);
		RootPanel.get(ADD_CLIENT_FORM_ID).add(addViewButton);

		usernameField.setFocus(true);
		usernameField.selectAll();

		// get the client views container
		final RootPanel viewsContainer = RootPanel.get(VIEWS_CONTAINER_ID);
		
		// Create a handler for the sendButton and nameField
		class AddClientViewHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on the addButton.
			 */
			public void onClick(ClickEvent event) {
				tryToGetView();
			}

			/**
			 * Try to get view from the server and wait for a response.
			 */
			private void tryToGetView() {
				addViewButton.setEnabled(false);
				String username = usernameField.getText();
				boolean wrapDoubleQuotes = true; // way to wrap the JSON parameter in javaScript
				final String wrapQuot = wrapDoubleQuotes ? "\"" : "'"; 
				clientViewService.getClientView(username, wrapDoubleQuotes, 
						new AsyncCallback<WavesClientViewContainer>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								//errorBox.removeStyleName("no-errors");
								errorBox.setStylePrimaryName("have-errors");
								errorBox.add(new HTML("<span>" + "RPC call failure: " +
										caught.getClass() + ": " +
										caught.getMessage() +
										(caught.getCause() != null
											? " (" + caught.getCause().getClass() + ":" + caught.getCause().getMessage() + ")"
											: ""	
										) + "</span>"));
								addViewButton.setEnabled(true);								
							}

							public void onSuccess(WavesClientViewContainer clientView) {
								/* clientIdToHolder.put(Integer.valueOf(clientView.getClientId()), 
												     clientView.getHolderElementId()); */
								viewsContainer.add(new HTML(
											"<script type='text/javascript'>"
											+ CLIENT_RENDER_JS_FUNC + 
											"(" + wrapQuot + clientView.getModelAsJSON() + wrapQuot + ");" +
											"</script>"
										));
								addViewButton.setEnabled(true);
							}
						});
			}
		}

		AddClientViewHandler avHandler = new AddClientViewHandler();
		addViewButton.addClickHandler(avHandler);
	}
			
}
