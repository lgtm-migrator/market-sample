import React from 'react';
import { Layout, Avatar } from "antd";
import { UserOutlined } from '@ant-design/icons';
import {Link} from "react-router-dom";

import headerLogo from "../assets/header.svg"

const { Header } = Layout

function AppHeader() {
  return (
    <Header style={{backgroundColor: "#971212"}}>
      <Link to="/">
        <img alt="demo store" src={headerLogo} style={{ height: "60px" }} />
      </Link>
      <div style={{ float: "right", color: "#971212" }}>
        <Avatar icon={<UserOutlined />} />
        <Link to="/user" style={{ marginLeft: "12px", color: "#ffffff" }}>
          View Profile
        </Link>
      </div>
    </Header>
  )
}

export default AppHeader;
