import React, {useEffect, useState} from 'react';
import {Button, Divider, Layout, Space, Table} from "antd";
import AppHeader from "../components/AppHeader";
import axios from "axios";
import {ORDERS_API_URL, PUSH_DISBURSEMENT_CLAIMS_API_URL} from "../consts";

const { Footer } = Layout;

function Admin() {

  const columns = [
    {
      title: 'Internal ID',
      dataIndex: 'local-id',
      key: 'local-id',
    },
    // {
    //   title: 'Credify ID',
    //   dataIndex: 'credify-id',
    //   key: 'credify-id',
    // },
    {
      title: 'Order ID',
      dataIndex: 'order-id',
      key: 'order-id',
    },
    {
      title: 'Order status',
      dataIndex: 'order-status',
      key: 'order-status',
    },
    {
      title: 'Action',
      key: 'action',
      render: (_: any, record: any) => (
        <Space size="middle">
          <Button disabled={record['order-status'] !== "APPROVED"} onClick={() => handleDisbursement(record['order-id'])}><a>Upload docs for disbursement</a></Button>
          <Button disabled={record['order-status'] !== "APPROVED"} onClick={() => handleCancellation(record['order-id'])}><a>Cancel</a></Button>
        </Space>
      ),
    },
  ];

  const [orders, setOrders] = useState([])

  useEffect(() => {
    axios.get(ORDERS_API_URL).then((res) => {
      if (res.data.orders) {
        const list = res.data.orders.reverse().map((order: any) => {
          return {
            key: order.id,
            'local-id': order.localId,
            'order-id': order.orderId,
            'order-status': order.status,
          }
        })
        setOrders(list)
      }
    })
  }, [])

  const handleDisbursement = (orderId: string) => {
    const body = {
      order_id: orderId,
      documents: {
        invoice: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        down_payment: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        delivery: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
      }
    };

    axios.post(`${PUSH_DISBURSEMENT_CLAIMS_API_URL}`, body).then((r) => {
      axios.get(ORDERS_API_URL).then((res) => {
        if (res.data.orders) {
          const list = res.data.orders.reverse().map((order: any) => {
            return {
              key: order.id,
              'local-id': order.localId,
              'order-id': order.orderId,
              'order-status': order.status,
            }
          })
          setOrders(list)
        }
      })

    })
  }

  const handleCancellation = (orderId: string) => {
    axios.post(`${ORDERS_API_URL}/${orderId}/cancel`, {}).then((res) => {
      console.log(res)
    })
  }

  return (
    <>
      <Layout>
        <AppHeader />
        <>
          <Divider orientation="center">Admin (demo)</Divider>
          <Table columns={columns} dataSource={orders} />

        </>
        <Footer style={{ marginTop: "16px", textAlign: "center", backgroundColor: "#F6E6D8" }}>This is Demo Store for serviceX BNPL</Footer>
      </Layout>
    </>
  )

}

export default Admin;
