# ImageSearch

Problem statement
The home screen of the app has a search field and an options menu. The functionality of the app are as follows:

Search
When the user types a search term in the search field and presses done the application communicates with a image search API (you can use Flickr API, Google images API or any other API you prefer) and displays the result images in a grid-layout below the search field. The images must be shown as square views in the grid without any skewing

Options
The number of columns in the grid can be changed to 2, 3 or 4 columns from the options menu (by default the grid is 2 column). Changing the number of columns should NOT re-invoke the API, it must be handled at the UI level.

Paging
The grid layout has infinite scroll support i.e. if the user scrolls to the bottom of the page, then the app will re-contact the image search API to get more results and add them to the bottom of the grid.

Offline Support
Whatever terms the user has searched as well as the associated search results need to be persisted (the choice of persistence is left to the developer). So if the user is offline and performs a search for a term for which the results were saved earlier, the app will display the saved results maintaining the order as it was fetched from the API.

Bonus Components:
These are optional and should be attempted only if time permits:

Bonus #1
When the user clicks on an image in the grid, perform a shared element transition of the square image to a full screen image on a new activity. Note that the full screen image is not square, it maintains the original aspect of the image, so the shared element transition must take this into account. Pressing back from the full screen activity should do a reverse transition from the full screen image back to the square image in the grid.

Bonus #2
Once on the full screen activity the user can swipe left/right to see the next/previous images. The reverse shared element transition should respect the current image visible on the full screen.

