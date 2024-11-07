"use client"

import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const WebSocketListener = ({ userId }) => {
    const [messages, setMessages] = useState([]);
    const socketUrl = 'http://localhost:8080/ws';

    useEffect(() => {
        // Establish a WebSocket connection
        const socket = new SockJS(socketUrl);
        const stompClient = Stomp.over(socket);

        // Connect and subscribe to the user's notification channel
        stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            userId = 1;
            // Subscribe to the specific user notifications
            stompClient.subscribe(`/user/${userId}/notifications`, (message) => {
                const receivedMessage = JSON.parse(message.body);
                setMessages((prevMessages) => [...prevMessages, receivedMessage]);
                console.log('Received notification:', receivedMessage);
            });
        }, (error) => {
            console.error('Error connecting to WebSocket:', error);
        });

        // Cleanup function to disconnect WebSocket on unmount
        return () => {
            if (stompClient.connected) {
                stompClient.disconnect(() => {
                    console.log('Disconnected from WebSocket');
                });
            }
        };
    }, [userId]);

    return (
        <div>
            <h3>Notifications</h3>
            <ul>
                {messages.map((msg, index) => (
                    <li key={index}>{msg.message}</li>
                ))}
            </ul>
        </div>
    );
};

export default WebSocketListener;
