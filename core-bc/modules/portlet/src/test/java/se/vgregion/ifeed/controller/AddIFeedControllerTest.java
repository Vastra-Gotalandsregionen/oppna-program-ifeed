package se.vgregion.ifeed.controller;

import org.junit.Ignore;

@Ignore
public class AddIFeedControllerTest {

	/*AddIFeedController controller;
	private IFeedService iFeedService;
	private ResourceLocalService resourceLocalService;
	private RenderResponse response;

	@Before
	public void setUp() throws Exception {
		iFeedService = mock(IFeedService.class);
		resourceLocalService = mock(ResourceLocalService.class);
		controller = new AddIFeedController(iFeedService, resourceLocalService) {
			@Override
			protected User fetchUser(PortletRequest request) throws PortalException, SystemException {
				User user = Mockito.mock(User.class);
				return user;
			}
		};
		response = mock(RenderResponse.class);
	}

	@Test
	public void getCommandObject() {
		IFeed feed = controller.getCommandObject();
	}

	@Test
	public void showAddIFeedForm() {
		String result = controller.showAddIFeedForm(response);
		assertEquals("addIFeedForm", result);
	}

	// @Test
	// public void initBinder() {
	// WebDataBinder binder = mock(WebDataBinder.class);
	// controller.initBinder(binder);
	// }

	@Test
	public void createNewIFeed() {
		SessionStatus sessionStatus = mock(SessionStatus.class);
		Model model = mock(Model.class);
		ActionResponse actionResponse = mock(ActionResponse.class);

		controller.createNewIFeed(actionResponse, sessionStatus, model);
		verify(model).addAttribute(eq("ifeed"), any(IFeed.class));
	}

	@Test
	public void testAddIFeed() {
		Model model = mock(Model.class);
		IFeed iFeedIn = mock(IFeed.class);
		BindingResult bindingResult = mock(BindingResult.class);
		ActionRequest request = mock(ActionRequest.class);
		ActionResponse actionResponse = mock(ActionResponse.class);
		ThemeDisplay themeDisplay = mock(ThemeDisplay.class);

		when((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).thenReturn(themeDisplay);
		IFeed newFeed = new IFeed();
		newFeed.setId(101l);
		when(iFeedService.addIFeed(any(IFeed.class))).thenReturn(newFeed);

		controller.addIFeed(model, iFeedIn, bindingResult, request, actionResponse);
		verify(model).addAttribute(eq("ifeed"), eq(newFeed));
	}*/

}
