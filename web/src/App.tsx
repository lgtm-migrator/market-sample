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

function App() {

  const [user, setUser] = useState<UserProfile | null>(null)

  const loadNewUser = () => {
    axios.get(DEMO_USER_API_URL).then((res) => {
      let user = res.data;
      user["countryCode"] = user.phoneCountryCode
      localStorage.setItem(USER_CACHE_KEY, JSON.stringify(user))
      setUser(res.data);
    })
  }

  useEffect(() => {
    initialize(Environment.SIT, API_KEY)

    if (localStorage.getItem(USER_CACHE_KEY)) {
      setUser(JSON.parse(localStorage.getItem(USER_CACHE_KEY) || ""))
    } else {
      loadNewUser();
    }
  }, [])

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Checkout user={user || {}} />} />
        <Route path="/user" element={<User user={user || {}} loadNewUser={loadNewUser} />} />
        <Route path="/callback" element={<Callback />} />
        <Route path="/checkout" element={<Checkout user={user || {}} />} />
        <Route path="/admin" element={<Admin />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
