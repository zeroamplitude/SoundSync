package net.uoit.distributedsystems.soundsync.app.peers.tree;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by nicholas on 27/11/15.
 */
public class PeerTree {

    private Peer superPeer;

    public PeerTree(InetAddress superPeerAddress) {
        this.superPeer = new Peer(superPeerAddress);
    }

    public InetAddress addPeerToTree(InetAddress peerToAdd) {
        return visitNode(this.superPeer, new Peer(peerToAdd));
    }

    public InetAddress visitNode(Peer currentNode, Peer peerToAdd) {
        if(currentNode.getLeftChild() == null) {
            currentNode.setLeftChild(peerToAdd);
            return currentNode.getAddress();
        }
        if(currentNode.getRightChild() == null) {
            currentNode.setRightChild(peerToAdd);
            return currentNode.getAddress();
        }
        return visitNode(currentNode.left, peerToAdd);
    }


    public class Peer {

        private InetAddress address;
        private ArrayList<Peer> peers;
        private Peer left = null;
        private Peer right = null;

        public Peer(InetAddress address) {
            this.address = address;
        }

        public InetAddress getAddress() {
            return address;
        }

        public void addPeer(Peer peer) {
            peers.add(peer);
        }

        public Peer getLeftChild() {
            return left;
        }

        public Peer getRightChild() {
            return right;
        }

        public void setLeftChild(Peer peer) {
            left = peer;
            addPeer(peer);
        }

        public void setRightChild(Peer peer) {
            right = peer;
            addPeer(peer);
        }
    }
}
