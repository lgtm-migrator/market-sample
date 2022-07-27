import React from 'react';
import {Divider, Layout} from "antd";
import AppHeader from "../components/AppHeader";

const { Footer } = Layout;

function Callback() {

  return (
    <>
      <Layout>
        <AppHeader />

        <>
          <Divider orientation="center">Success</Divider>
        </>

        <Footer style={{ marginTop: "16px", textAlign: "center", backgroundColor: "#F6E6D8" }}>This is Demo Store for serviceX BNPL</Footer>
      </Layout>
    </>
  )

}

export default Callback;
