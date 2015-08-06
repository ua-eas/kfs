var MyFavorites = React.createClass({
    render() {
        return (
            <h2>My Favorites</h2>
        )
    }
});

if (document.getElementById('my-favorites')) {
    React.render(
        <MyFavorites/>,
        document.getElementById('my-favorites')
    );
}

export default MyFavorites;