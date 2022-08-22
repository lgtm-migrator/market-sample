import React, {useEffect, useState} from 'react';
import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";
import axios from "axios";
import './App.css';
// import Home from "./pages/Home";
import Callback from "./pages/Callback";
import Checkout from "./pages/Checkout";
import User from "./pages/User";
import Admin from "./pages/Admin";
import {API_KEY, DEMO_USER_API_URL, USER_CACHE_KEY} from "./consts";
import {Environment, initialize, UserProfile} from "credify-web-sdk";

initialize(Environment.SIT, API_KEY)
function App() {
  const [user, setUser] = useState<UserProfile | null>(null)

  const loadUser = (id?: string) => {
    let url = DEMO_USER_API_URL
    if (id) {
      url = `${url}?id=${id}`
    }
    axios.get(url).then((res) => {
      let user = res.data;
      user["countryCode"] = user.phoneCountryCode
      localStorage.setItem(USER_CACHE_KEY, JSON.stringify(user))
      setUser(res.data);
    })
  }

  useEffect(() => {
    if (localStorage.getItem(USER_CACHE_KEY)) {
      const u = JSON.parse(localStorage.getItem(USER_CACHE_KEY) || "")
      loadUser(`${u.id}`)
    } else {
      loadUser();
    }
  }, [])

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Checkout user={user || {}} />} />
        <Route path="/user" element={<User user={user || {}} loadNewUser={loadUser} />} />
        <Route path="/callback" element={<Callback />} />
        <Route path="/checkout" element={<Checkout user={user || {}} />} />
        <Route path="/admin" element={<Admin />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
